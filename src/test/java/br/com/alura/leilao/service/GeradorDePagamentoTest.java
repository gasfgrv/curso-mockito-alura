package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeradorDePagamentoTest {

    private GeradorDePagamento gerador;

    @Mock
    private PagamentoDao dao;

    @Mock
    private Clock clock;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        gerador = new GeradorDePagamento(dao, clock);


    }

    @Test
    void deveriaCriarPagamentoParaVendorDoLeilao() {
        Leilao leilao = leilao();
        Lance vencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2020, 12, 7);

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(clock.instant()).thenReturn(instant);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        gerador.gerarPagamento(vencedor);

        verify(dao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        assertEquals(LocalDate.now(clock).plusDays(1), pagamento.getVencimento());
        assertEquals(vencedor.getValor(), pagamento.getValor());
        assertFalse(pagamento.getPago());
        assertEquals(vencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(leilao, pagamento.getLeilao());
    }

    // TODO: 12/11/2021 vencimento no sabado
    @Test
    void deveriaCriarPagamentoParaVendorDoLeilaoComVencimentoNoSabado() {
        Leilao leilao = leilao();
        Lance vencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2020, 12, 4);

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(clock.instant()).thenReturn(instant);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        gerador.gerarPagamento(vencedor);

        verify(dao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        assertEquals(LocalDate.now(clock).plusDays(3), pagamento.getVencimento());
        assertEquals(vencedor.getValor(), pagamento.getValor());
        assertFalse(pagamento.getPago());
        assertEquals(vencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(leilao, pagamento.getLeilao());
    }

    // TODO: 12/11/2021 vencimento no domingo
    @Test
    void deveriaCriarPagamentoParaVendorDoLeilaoComVencimentoNoDomingo() {
        Leilao leilao = leilao();
        Lance vencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2020, 12, 5);

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(clock.instant()).thenReturn(instant);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        gerador.gerarPagamento(vencedor);

        verify(dao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        assertEquals(LocalDate.now(clock).plusDays(2), pagamento.getVencimento());
        assertEquals(vencedor.getValor(), pagamento.getValor());
        assertFalse(pagamento.getPago());
        assertEquals(vencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(leilao, pagamento.getLeilao());
    }

    private Leilao leilao() {
        Leilao leilao = new Leilao("Teste", new BigDecimal("100"), new Usuario("User"));
        Lance lanceVencedor = new Lance(new Usuario("user2"), new BigDecimal("300"));
        leilao.propoe(lanceVencedor);
        leilao.setLanceVencedor(lanceVencedor);
        return leilao;
    }
}