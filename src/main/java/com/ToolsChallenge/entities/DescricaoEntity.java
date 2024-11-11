package com.ToolsChallenge.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "descricao_pagamento")
@AllArgsConstructor
@NoArgsConstructor
public class DescricaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_descricao_pagamento")
    private long id;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String estabelecimento;

    @Column(name = "nsu", nullable = false, unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nsu;


    @Column(name = "codigo_autorizacao", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String codigoAutorizacao;
}
