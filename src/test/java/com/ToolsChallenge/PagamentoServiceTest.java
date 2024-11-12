package com.toolschallenge;

import com.toolschallenge.dto.PagamentoDTO;
import com.toolschallenge.entities.DescricaoEntity;
import com.toolschallenge.entities.FormaPagamentoEntity;
import com.toolschallenge.entities.PagamentoEntity;
import com.toolschallenge.enums.TipoPagamento;
import com.toolschallenge.mappers.PagamentoMapper;
import com.toolschallenge.repository.DescricaoRepository;
import com.toolschallenge.repository.FormaPagamentoRepository;
import com.toolschallenge.repository.PagamentoRepository;
import com.toolschallenge.service.PagamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private FormaPagamentoRepository formaPagamentoRepository;

    @Mock
    private DescricaoRepository descricaoRepository;

    @Mock
    private PagamentoMapper pagamentoMapper;

    @InjectMocks
    private PagamentoService pagamentoService;

    private PagamentoDTO pagamentoDTO;
    private PagamentoEntity pagamentoEntity;
    private DescricaoEntity descricaoEntity;
    private FormaPagamentoEntity formaPagamentoEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        descricaoEntity = new DescricaoEntity();
        descricaoEntity.setValor(500.0);
        descricaoEntity.setDataHora(LocalDateTime.now());
        descricaoEntity.setEstabelecimento("Joao mercado");

        formaPagamentoEntity = new FormaPagamentoEntity();
        formaPagamentoEntity.setTipo(TipoPagamento.AVISTA);
        formaPagamentoEntity.setParcela(1);

        pagamentoEntity = new PagamentoEntity();
        pagamentoEntity.setCartao("12345678");
        pagamentoEntity.setDescricao(descricaoEntity);
        pagamentoEntity.setFormaPagamento(formaPagamentoEntity);

        pagamentoDTO = new PagamentoDTO(
                pagamentoEntity.getId(),
                pagamentoEntity.getCartao(),
                pagamentoEntity.getDescricao(),
                pagamentoEntity.getFormaPagamento());
    }

    @Test
    void testRealizarPagamento() {

        when(pagamentoRepository.existsById(pagamentoEntity.getId())).thenReturn(false);
        when(descricaoRepository.save(any(DescricaoEntity.class))).thenReturn(descricaoEntity);
        when(formaPagamentoRepository.save(any(FormaPagamentoEntity.class))).thenReturn(formaPagamentoEntity);
        when(pagamentoRepository.save(any(PagamentoEntity.class))).thenReturn(pagamentoEntity);
        when(pagamentoMapper.toDto(any(PagamentoEntity.class))).thenReturn(pagamentoDTO);
        when(pagamentoMapper.toEntity(any(PagamentoDTO.class))).thenReturn(pagamentoEntity);

        PagamentoDTO result = pagamentoService.realizarPagamento(pagamentoDTO);

        assertNotNull(result);
        assertEquals(pagamentoEntity.getId(), result.id());
        assertEquals(pagamentoEntity.getCartao(), result.cartao());

        assertEquals(descricaoEntity.getId(), result.descricao().getId());
        assertEquals(descricaoEntity.getValor(), result.descricao().getValor());
        assertEquals(descricaoEntity.getDataHora(), result.descricao().getDataHora());
        assertEquals(descricaoEntity.getEstabelecimento(), result.descricao().getEstabelecimento());
        assertEquals(descricaoEntity.getNsu(), result.descricao().getNsu());
        assertEquals(descricaoEntity.getCodigoAutorizacao(), result.descricao().getCodigoAutorizacao());
        assertEquals(descricaoEntity.getStatusPagamento(), result.descricao().getStatusPagamento());

        assertEquals(formaPagamentoEntity.getIdFormaPagamento(), result.formaPagamento().getIdFormaPagamento());
        assertEquals(formaPagamentoEntity.getTipo(), result.formaPagamento().getTipo());
        assertEquals(formaPagamentoEntity.getParcela(), result.formaPagamento().getParcela());
    }


    @Test
    void testRealizarPagamentoIdExistente() {

        when(pagamentoRepository.existsById(pagamentoEntity.getId())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.realizarPagamento(pagamentoDTO);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Pagamento com ID " + pagamentoEntity.getId() + " já existe.", exception.getReason());
    }

    @Test
    void testRealizarPagamentoErroBanco() {

        when(pagamentoRepository.existsById(pagamentoEntity.getId())).thenReturn(false);
        when(descricaoRepository.save(any(DescricaoEntity.class))).thenThrow(new DataAccessException("Erro no banco") {
        });

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.realizarPagamento(pagamentoDTO);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Erro ao acessar o banco de dados durante a realização do pagamento.", exception.getReason());
    }

    @Test
    void testRealizarEstorno() {

        when(pagamentoRepository.getReferenceById(pagamentoEntity.getId())).thenReturn(pagamentoEntity);
        when(descricaoRepository.getReferenceById(descricaoEntity.getId())).thenReturn(descricaoEntity);
        when(descricaoRepository.save(any(DescricaoEntity.class))).thenReturn(descricaoEntity);
        when(pagamentoMapper.toDto(any(PagamentoEntity.class))).thenReturn(pagamentoDTO);
        when(pagamentoMapper.toEntity(any(PagamentoDTO.class))).thenReturn(pagamentoEntity);

        PagamentoDTO result = pagamentoService.realizarEstorno(pagamentoEntity.getId());

        assertNotNull(result);
        assertEquals(descricaoEntity.getId(), result.descricao().getId());
        assertEquals(descricaoEntity.getValor(), result.descricao().getValor());
        assertEquals(descricaoEntity.getDataHora(), result.descricao().getDataHora());
        assertEquals(descricaoEntity.getEstabelecimento(), result.descricao().getEstabelecimento());
        assertEquals(descricaoEntity.getNsu(), result.descricao().getNsu());
        assertEquals(descricaoEntity.getCodigoAutorizacao(), result.descricao().getCodigoAutorizacao());
        assertEquals(descricaoEntity.getStatusPagamento(), result.descricao().getStatusPagamento());

        assertEquals(formaPagamentoEntity.getIdFormaPagamento(), result.formaPagamento().getIdFormaPagamento());
        assertEquals(formaPagamentoEntity.getTipo(), result.formaPagamento().getTipo());
        assertEquals(formaPagamentoEntity.getParcela(), result.formaPagamento().getParcela());
    }

    @Test
    void testRealizarEstornoPagamentoNaoEncontrado() {

        when(pagamentoRepository.getReferenceById(pagamentoEntity.getId())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento não encontrado"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.realizarEstorno(pagamentoEntity.getId());
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Pagamento não encontrado", exception.getReason());
    }

    @Test
    void testBucarTodosPagamentos() {

        when(pagamentoRepository.findAll()).thenReturn(List.of(pagamentoEntity));
        when(pagamentoMapper.toDto(any(PagamentoEntity.class))).thenReturn(pagamentoDTO);

        var result = pagamentoService.bucarTodosPagamentos();

        assertNotNull(result);
        assertEquals(1, result.size());

        List<PagamentoDTO> resultList = new ArrayList<>(result);

        assertEquals(descricaoEntity.getId(), resultList.get(0).descricao().getId());
        assertEquals(descricaoEntity.getValor(), resultList.get(0).descricao().getValor());
        assertEquals(descricaoEntity.getDataHora(), resultList.get(0).descricao().getDataHora());
        assertEquals(descricaoEntity.getEstabelecimento(), resultList.get(0).descricao().getEstabelecimento());
        assertEquals(descricaoEntity.getNsu(), resultList.get(0).descricao().getNsu());
        assertEquals(descricaoEntity.getCodigoAutorizacao(), resultList.get(0).descricao().getCodigoAutorizacao());
        assertEquals(descricaoEntity.getStatusPagamento(), resultList.get(0).descricao().getStatusPagamento());
        assertEquals(formaPagamentoEntity.getIdFormaPagamento(), resultList.get(0).formaPagamento().getIdFormaPagamento());
        assertEquals(formaPagamentoEntity.getTipo(), resultList.get(0).formaPagamento().getTipo());
        assertEquals(formaPagamentoEntity.getParcela(), resultList.get(0).formaPagamento().getParcela());
    }


    @Test
    void testBuscarPagamentoID() {

        when(pagamentoRepository.findById(pagamentoEntity.getId())).thenReturn(Optional.of(pagamentoEntity));
        when(pagamentoMapper.toDto(any(PagamentoEntity.class))).thenReturn(pagamentoDTO);

        PagamentoDTO result = pagamentoService.buscarPagamentoID(pagamentoEntity.getId());

        assertNotNull(result);
        assertEquals(descricaoEntity.getId(), result.descricao().getId());
        assertEquals(descricaoEntity.getValor(), result.descricao().getValor());
        assertEquals(descricaoEntity.getDataHora(), result.descricao().getDataHora());
        assertEquals(descricaoEntity.getEstabelecimento(), result.descricao().getEstabelecimento());
        assertEquals(descricaoEntity.getNsu(), result.descricao().getNsu());
        assertEquals(descricaoEntity.getCodigoAutorizacao(), result.descricao().getCodigoAutorizacao());
        assertEquals(descricaoEntity.getStatusPagamento(), result.descricao().getStatusPagamento());
        assertEquals(formaPagamentoEntity.getIdFormaPagamento(), result.formaPagamento().getIdFormaPagamento());
        assertEquals(formaPagamentoEntity.getTipo(), result.formaPagamento().getTipo());
        assertEquals(formaPagamentoEntity.getParcela(), result.formaPagamento().getParcela());
    }

    @Test
    void testBuscarPagamentoIDPagamentoNaoEncontrado() {

        when(pagamentoRepository.findById(pagamentoEntity.getId())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pagamentoService.buscarPagamentoID(pagamentoEntity.getId());
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Pagamento " + pagamentoEntity.getId() + " não encontrado.", exception.getReason());
    }
}
