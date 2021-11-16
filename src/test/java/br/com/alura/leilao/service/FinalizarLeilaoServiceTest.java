package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService service;

    @Mock
    private LeilaoDao dao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new FinalizarLeilaoService(dao, enviadorDeEmails);
    }

    @Test
    @DisplayName("Deve finalizar um leilão")
    void deveFinalizarUmLeilao() {
        List<Leilao> lista = leiloes();
        when(dao.buscarLeiloesExpirados()).thenReturn(lista);
        service.finalizarLeiloesExpirados();
        Leilao leilao = lista.get(0);
        assertTrue(leilao.isFechado());
        assertEquals(new BigDecimal("300"), leilao.getLanceVencedor().getValor());
        verify(dao).salvar(leilao);
    }

    @Test
    @DisplayName("Deveria enviar e-mail para o vencedor do leilão")
    void deveriaEnviarEmailParaVencedorLeilao() {
        List<Leilao> lista = leiloes();

        when(dao.buscarLeiloesExpirados()).thenReturn(lista);

        service.finalizarLeiloesExpirados();

        Leilao leilao = lista.get(0);
        Lance lanceVencedor = leilao.getLanceVencedor();

        verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor);
    }

    @Test
    @DisplayName("Não deveria enviar e-mail para o vencedor do leilão em caso de erro ao encerrar o leilão")
    void naoDeveriaEnviarEmailParaVencedorLeilaoEmCasoDeErroAoEncerrarOLeilao() {
        when(dao.buscarLeiloesExpirados()).thenReturn(leiloes());
        doThrow(new RuntimeException("Erro ao salvar")).when(dao).salvar(any());

        try {
            service.finalizarLeiloesExpirados();
            fail();
        } catch (RuntimeException e) {
            verifyNoInteractions(enviadorDeEmails);
        }
    }

    private List<Leilao> leiloes() {
        Leilao leilao = new Leilao("Teste", new BigDecimal("100"), new Usuario("User"));

        List<Lance> lances = Arrays.asList(
                new Lance(new Usuario("user1"), new BigDecimal("200")),
                new Lance(new Usuario("user2"), new BigDecimal("300"))
        );

        leilao.propoe(lances.get(0));
        leilao.propoe(lances.get(1));

        return Collections.singletonList(leilao);
    }

}