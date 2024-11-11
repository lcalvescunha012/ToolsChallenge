package com.ToolsChallenge.entities;

import com.ToolsChallenge.enums.TipoPagamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "formaPagamento")
@AllArgsConstructor
@NoArgsConstructor
public class FormaPagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_forma_pagamento")
    @JsonIgnore
    private long idFormaPagamento;

    @Column(nullable = false)
    private TipoPagamento tipo;

    @Column(nullable = false)
    private Integer parcela;


}
