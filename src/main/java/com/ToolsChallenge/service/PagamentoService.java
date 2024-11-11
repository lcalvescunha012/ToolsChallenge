package com.ToolsChallenge.service;

import com.ToolsChallenge.dto.PagamentoDTO;
import com.ToolsChallenge.entities.DescricaoEntity;
import com.ToolsChallenge.entities.FormaPagamentoEntity;
import com.ToolsChallenge.entities.PagamentoEntity;
import com.ToolsChallenge.enums.StatusPagamento;
import com.ToolsChallenge.mappers.PagamentoMapper;
import com.ToolsChallenge.repository.DescricaoRepository;
import com.ToolsChallenge.repository.FormaPagamentoRepository;
import com.ToolsChallenge.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            descricao = salvaDescricao(descricao, StatusPagamento.AUTORIZADO.name());

            FormaPagamentoEntity formaPagamento = pagamentoDTO.formaPagamento();
            formaPagamento = salvarFormaPagamento(formaPagamento);

            if (descricao == null || formaPagamento == null) {
                throw new NullPointerException("Erro no processamento do JSON. Valores incompletos");

            }

            PagamentoEntity pagamentoEntity = pagamentoMapper.toEntity(pagamentoDTO);
            pagamentoEntity.setDescricao(descricao);
            pagamentoEntity.setFormaPagamento(formaPagamento);

            PagamentoEntity retornoPagamento = pagamentoRepository.save(pagamentoEntity);

            return pagamentoMapper.toDto(retornoPagamento);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos fornecidos no pagamento.", e);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados durante a realização do pagamento.", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado durante a realização do pagamento.", e);
        }
    }


    private DescricaoEntity salvaDescricao(DescricaoEntity descricao, String status) {

        // Gera valores únicos para NSU e código de autorização se não forem informados
        atualizarValoresUnicos(descricao);

        atualizarStatusPagamento(descricao, status);

        return descricaoRepository.save(descricao);
    }

    private void atualizarValoresUnicos(DescricaoEntity descricao) {
        // Gera um NSU único se não for fornecido
        if (descricao.getNsu() == null || descricao.getNsu().isEmpty()) {
            descricao.setNsu(formataUUID(UUID.randomUUID().toString(), LENGHT_NSU));
        }

        // Gera um código de autorização único se não for fornecido
        if (descricao.getCodigoAutorizacao() == null || descricao.getCodigoAutorizacao().isEmpty()) {
            descricao.setCodigoAutorizacao(formataUUID(UUID.randomUUID().toString(), LENGHT_COD_AUTO));
        }
    }

    private String formataUUID(String uuidToReplace, int i) {
        String uuid = uuidToReplace.replace("-", "");
        String uuidReplaced = uuid.replaceAll("[^0-9]", "");
        return uuidReplaced.length() > i ? uuidReplaced.substring(0, i) : uuidReplaced;
    }

    protected FormaPagamentoEntity salvarFormaPagamento(FormaPagamentoEntity formaPagamento) {

        return formaPagamentoRepository.save(formaPagamento);
    }


    private void atualizarStatusPagamento(DescricaoEntity descricao, String status) {
        try {
            StatusPagamento statusPagamento = StatusPagamento.valueOf(status);

            descricao.setStatusPagamento(statusPagamento);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Valor diferente de AUTORIZADO/NEGADO/CANCELADO.");
        }
    }

    public PagamentoDTO realizarEstorno(long id) {
        PagamentoEntity pagamentoEntity;
        DescricaoEntity descricaoEntity;
        PagamentoDTO pagamentoDTO;

        try {

            pagamentoEntity = pagamentoRepository.getReferenceById(id);

            if (pagamentoEntity == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento com ID " + id + " não encontrado.");
            }
            descricaoEntity = descricaoRepository.getReferenceById(pagamentoEntity.getDescricao().getId());

            if (descricaoEntity == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Descrição do pagamento com ID " + id + " não encontrada.");
            }

            salvaDescricao(descricaoEntity, StatusPagamento.CANCELADO.name());


            pagamentoEntity.setDescricao(descricaoEntity);
            pagamentoDTO = pagamentoMapper.toDto(pagamentoEntity);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Houve problema com o pagamento " + id + " para realizar o estorno.", e);
        }

        return pagamentoDTO;
    }


    public Collection<PagamentoDTO> finaAll() {
        return pagamentoRepository.findAll().stream().map(pagamentoMapper::toDto).collect(Collectors.toList());
    }

    public PagamentoDTO findById(long id) {
        return pagamentoMapper.toDto(pagamentoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento " + id + " não encontrado.")));
    }
}
