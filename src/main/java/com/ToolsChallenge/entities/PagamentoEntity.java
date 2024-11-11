package com.ToolsChallenge.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "pagamento")
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoEntity {

    @Id
    @Column(nullable = false, unique = true)
    private long id;

    private String cartao;

    @OneToOne
    @JoinColumn(name = "id_descricao_pagamento", nullable = false)
    private DescricaoEntity descricao;

    @OneToOne
    @JoinColumn(name = "id_forma_pagamento", nullable = false)
    private FormaPagamentoEntity formaPagamento;
}
