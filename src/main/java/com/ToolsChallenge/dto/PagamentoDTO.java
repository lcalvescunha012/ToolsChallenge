package com.ToolsChallenge.dto;

import com.ToolsChallenge.entities.DescricaoEntity;
import com.ToolsChallenge.entities.FormaPagamentoEntity;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public record PagamentoDTO(
        @NotNull long id,
        @NotBlank(message = "O Cartão tem que estar preenchido.") String cartao,
        DescricaoEntity descricao,
        FormaPagamentoEntity formaPagamento
) {
}
