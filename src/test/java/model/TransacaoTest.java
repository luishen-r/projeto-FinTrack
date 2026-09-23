package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransacaoTest {

    @Test
    void construtorSemDataDeveAssumirHojeComoData() {
        Transacao transacao = new Transacao("Salario", 1500.0, "receita");

        assertEquals(LocalDate.now(), transacao.getData());
    }

    @Test
    void construtorComDataDeveArmazenarADataInformada() {
        LocalDate data = LocalDate.of(2026, 9, 10);
        Transacao transacao = new Transacao("Salario", 1500.0, "receita", data);

        assertEquals(data, transacao.getData());
    }

    @Test
    void setDataDeveAtualizarAData() {
        Transacao transacao = new Transacao("Salario", 1500.0, "receita");
        LocalDate novaData = LocalDate.of(2026, 1, 1);

        transacao.setData(novaData);

        assertEquals(novaData, transacao.getData());
    }

    @Test
    void construtorDeveArmazenarOsValoresCorretamente() {
        Transacao transacao = new Transacao("Salario", 1500.0, "receita");

        assertEquals("Salario", transacao.getDescricao());
        assertEquals(1500.0, transacao.getValor());
        assertEquals("receita", transacao.getTipo());
    }

    @Test
    void settersDevemAtualizarOsValores() {
        Transacao transacao = new Transacao("Salario", 1500.0, "receita");

        transacao.setDescricao("Bonus");
        transacao.setValor(2000.0);
        transacao.setTipo("despesa");

        assertEquals("Bonus", transacao.getDescricao());
        assertEquals(2000.0, transacao.getValor());
        assertEquals("despesa", transacao.getTipo());
    }

    @Test
    void toStringDeveConterDescricaoTipoEValorFormatado() {
        Transacao transacao = new Transacao("Aluguel", 800.0, "despesa");

        String texto = transacao.toString();

        assertTrue(texto.contains("Aluguel"));
        assertTrue(texto.contains("despesa"));
    }
}
