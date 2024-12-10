package com.toolschallenge.service;


import com.toolschallenge.dto.PagamentoDTO;
import com.toolschallenge.entities.DescricaoEntity;
import com.toolschallenge.entities.FormaPagamentoEntity;
import com.toolschallenge.entities.PagamentoEntity;
import com.toolschallenge.enums.StatusPagamento;
import com.toolschallenge.mappers.PagamentoMapper;
import com.toolschallenge.repository.DescricaoRepository;
import com.toolschallenge.repository.FormaPagamentoRepository;
import com.toolschallenge.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagamentoService {
    public static final int LENGHT_NSU = 10;
    public static final int LENGHT_COD_AUTO = 9;
    private final PagamentoRepository pagamentoRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;
    private final DescricaoRepository descricaoRepository;
    private final PagamentoMapper pagamentoMapper;

    @Transactional
    public PagamentoDTO realizarPagamento(PagamentoDTO pagamentoDTO) {

        try {
            if (pagamentoRepository.existsById(pagamentoDTO.id())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Pagamento com ID " + pagamentoDTO.id() + " já existe.");
            }

            DescricaoEntity descricao = pagamentoDTO.descricao();
            descricao = salvarDescricao(descricao, StatusPagamento.AUTORIZADO.name());

            FormaPagamentoEntity formaPagamento = pagamentoDTO.formaPagamento();
            formaPagamento = salvarFormaPagamento(formaPagamento);

            PagamentoEntity pagamentoEntity = pagamentoMapper.toEntity(pagamentoDTO);
            pagamentoEntity.setDescricao(descricao);
            pagamentoEntity.setFormaPagamento(formaPagamento);

            PagamentoEntity retornoPagamento = pagamentoRepository.save(pagamentoEntity);

            return pagamentoMapper.toDto(retornoPagamento);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos fornecidos no pagamento.", e);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados durante a realização do pagamento.", e);
        } catch (Exception e) {
            throw e;
        }
    }


    private DescricaoEntity salvarDescricao(DescricaoEntity descricao, String status) {

        try {

            if (descricao == null) {
                throw new NullPointerException("Erro no processamento do JSON. Valores incompletos nos campos da descricao.");
            }

            atualizarValoresUnicos(descricao);

            atualizarStatusPagamento(descricao, status);

            return descricaoRepository.save(descricao);

        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados durante a realização do pagamento.", e);
        } catch (Exception e) {
            throw e;
        }
    }

    private void atualizarValoresUnicos(DescricaoEntity descricao) {

        if (descricao.getNsu() == null || descricao.getNsu().isEmpty()) {
            descricao.setNsu(formatarUUID(UUID.randomUUID().toString(), LENGHT_NSU));
        }

        if (descricao.getCodigoAutorizacao() == null || descricao.getCodigoAutorizacao().isEmpty()) {
            descricao.setCodigoAutorizacao(formatarUUID(UUID.randomUUID().toString(), LENGHT_COD_AUTO));
        }
    }

    private String formatarUUID(String uuidToReplace, int i) {
        String uuid = uuidToReplace.replace("-", "");
        String uuidReplaced = uuid.replaceAll("[^0-9]", "");
        return uuidReplaced.length() > i ? uuidReplaced.substring(0, i) : uuidReplaced;
    }

    protected FormaPagamentoEntity salvarFormaPagamento(FormaPagamentoEntity formaPagamento) {

        try {

            if (formaPagamento == null) {
                throw new NullPointerException("Erro no processamento do JSON. Valores incompletos Valores incompletos nos campos da formaPagamento.");
            }

            return formaPagamentoRepository.save(formaPagamento);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado durante o processamento do Forma Pagamento.", e);
        }
    }

    private void atualizarStatusPagamento(DescricaoEntity descricao, String status) {

        try {

            StatusPagamento statusPagamento = StatusPagamento.valueOf(status);

            if (isStatusPagamentoIgual(descricao, statusPagamento)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "O status do pagamento já está definido como: " + statusPagamento);
            }

            descricao.setStatusPagamento(statusPagamento);

        } catch (IllegalArgumentException e) {

            String statusPermitidos = Arrays.stream(StatusPagamento.values())
                    .map(Enum::name)
                    .collect(Collectors.joining("/"));

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Valor diferente de " + statusPermitidos);
        }
    }

    private boolean isStatusPagamentoIgual(DescricaoEntity descricao, StatusPagamento statusPagamento) {
        return descricao.getStatusPagamento() != null &&
                descricao.getStatusPagamento().name().equals(statusPagamento.name());
    }

    @Transactional
    public PagamentoDTO realizarEstorno(long id) {

        PagamentoEntity pagamentoEntity = null;
        DescricaoEntity descricaoEntity = null;
        PagamentoDTO pagamentoDTO = null;

        try {

            pagamentoEntity = pagamentoRepository.getReferenceById(id);
            descricaoEntity = descricaoRepository.getReferenceById(pagamentoEntity.getDescricao().getId());

            salvarDescricao(descricaoEntity, StatusPagamento.CANCELADO.name());

            pagamentoEntity.setDescricao(descricaoEntity);
            pagamentoDTO = pagamentoMapper.toDto(pagamentoEntity);

            return pagamentoDTO;

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não foi encontrado registros com esse ID " + id + ".");
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados durante a realização do pagamento.", e);
        } catch (Exception e) {
            throw e;
        }
    }


    public Collection<PagamentoDTO> bucarTodosPagamentos() {
        return pagamentoRepository.findAll().stream().map(pagamentoMapper::toDto).collect(Collectors.toList());
    }

    public PagamentoDTO buscarPagamentoID(final long id) {
        return pagamentoMapper.toDto(pagamentoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento " + id + " não encontrado.")));
    }
}
