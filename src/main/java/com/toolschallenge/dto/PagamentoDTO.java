package com.toolschallenge.dto;

import com.toolschallenge.entities.DescricaoEntity;
import com.toolschallenge.entities.FormaPagamentoEntity;
import jakarta.validation.constraints.NotNull;

public record PagamentoDTO(
        @NotNull long id,
        @NotNull String cartao,
        DescricaoEntity descricao,
        FormaPagamentoEntity formaPagamento
) {
}
