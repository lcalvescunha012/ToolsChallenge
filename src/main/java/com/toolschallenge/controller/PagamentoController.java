package com.toolschallenge.controller;

import com.toolschallenge.dto.PagamentoDTO;
import com.toolschallenge.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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

    @Operation(description = "Realiza o estorno do do Pagamento.")
    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> realizarEstorno(@PathVariable long id) {
        return ResponseEntity.ok(pagamentoService.realizarEstorno(id));
    }

    @Operation(description = "Retorna a lista de todos os pagamentos realizados.")
    @GetMapping
    public ResponseEntity<Collection<PagamentoDTO>> findAll() {
        return ResponseEntity.ok(pagamentoService.bucarTodosPagamentos());
    }

    @Operation(description = "Retorna o pagamento realizado.")
    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> findById(@PathVariable long id) {
        return ResponseEntity.ok(pagamentoService.buscarPagamentoID(id));
    }
}
