package com.ToolsChallenge.mappers;

import com.ToolsChallenge.dto.PagamentoDTO;
import com.ToolsChallenge.entities.PagamentoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {
    public PagamentoDTO toDto(PagamentoEntity pagamentoEntity) {
        return new PagamentoDTO(
                pagamentoEntity.getId(),
                pagamentoEntity.getCartao(),
                pagamentoEntity.getDescricao(),
                pagamentoEntity.getFormaPagamento());
    }

    public PagamentoEntity toEntity(PagamentoDTO pagamentoDTOo) {
        return new PagamentoEntity(
                pagamentoDTOo.id(),
                pagamentoDTOo.cartao(),
                pagamentoDTOo.descricao(),
                pagamentoDTOo.formaPagamento());
    }
}
