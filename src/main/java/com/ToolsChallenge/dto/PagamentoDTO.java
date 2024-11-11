package com.ToolsChallenge.dto;

import com.ToolsChallenge.entities.DescricaoEntity;
import com.ToolsChallenge.entities.FormaPagamentoEntity;
import org.hibernate.validator.constraints.NotBlank;

public record PagamentoDTO(
        @NotBlank(message = "O ID tem que estar preenchido.") String id,
        @NotBlank(message = "O Cartão tem que estar preenchido.") String cartao,
        DescricaoEntity descricao,
        FormaPagamentoEntity formaPagamento
) {
}
