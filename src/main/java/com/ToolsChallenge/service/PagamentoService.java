package com.ToolsChallenge.service;

import com.ToolsChallenge.dto.PagamentoDTO;
import com.ToolsChallenge.entities.DescricaoEntity;
import com.ToolsChallenge.entities.FormaPagamentoEntity;
import com.ToolsChallenge.entities.PagamentoEntity;
import com.ToolsChallenge.mappers.PagamentoMapper;
import com.ToolsChallenge.repository.DescricaoRepository;
import com.ToolsChallenge.repository.FormaPagamentoRepository;
import com.ToolsChallenge.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
    public PagamentoDTO realizarPagamento(PagamentoDTO pagamento) {

        DescricaoEntity descricao = pagamento.descricao();
        descricao = salvaDescricao(descricao);

        FormaPagamentoEntity formaPagamento = pagamento.formaPagamento();
        formaPagamento = salvarFormaPagamento(formaPagamento);

        PagamentoEntity pagamentoEntity = pagamentoMapper.toEntity(pagamento);
        pagamentoEntity.setDescricao(descricao);
        pagamentoEntity.setFormaPagamento(formaPagamento);

        // Salva o pagamento e mapeia para DTO
        var entidade = pagamentoMapper.toEntity(pagamento);
        var salvo = pagamentoRepository.save(entidade);

        return pagamentoMapper.toDto(salvo);
    }



    private DescricaoEntity salvaDescricao(DescricaoEntity descricao) {

        // Gera valores únicos para NSU e código de autorização se não forem informados
        atualizarValoresUnicos(descricao);

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

    private String formataUUID(String string, int i) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String numero = uuid.replaceAll("[^0-9]", "");
        return numero.length() > i ? numero.substring(0, i) : numero;
    }

    private FormaPagamentoEntity salvarFormaPagamento(FormaPagamentoEntity formaPagamento) {

        return formaPagamentoRepository.save(formaPagamento);
    }

}
