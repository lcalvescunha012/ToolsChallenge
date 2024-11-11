package com.ToolsChallenge.controller;

import com.ToolsChallenge.dto.PagamentoDTO;
import com.ToolsChallenge.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/pagamento")
@AllArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @Operation(description = "Realiza o Pagamento.")
    @PostMapping
    public ResponseEntity<PagamentoDTO> realizarPagamento(@Valid @RequestBody PagamentoDTO pagamentoDTO) {
        return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatus.CREATED.value())).body(pagamentoService.realizarPagamento(pagamentoDTO));
    }
}
