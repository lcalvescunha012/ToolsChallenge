package com.ToolsChallenge.mappers;

import com.ToolsChallenge.dto.PagamentoDTO;
import com.ToolsChallenge.entities.PagamentoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {
    public PagamentoDTO toDto(PagamentoEntity pagamentoEntity) {
        return new PagamentoDTO(pagamentoEntity.getCartao(),
                pagamentoEntity.getId(),
                pagamentoEntity.getDescricao(),
                pagamentoEntity.getFormaPagamento());
    }

    public PagamentoEntity toEntity(PagamentoDTO pagamentoDTOo) {
        return new PagamentoEntity(pagamentoDTOo.cartao(),
                pagamentoDTOo.id(),
                pagamentoDTOo.descricao(),
                pagamentoDTOo.formaPagamento());
    }
}
