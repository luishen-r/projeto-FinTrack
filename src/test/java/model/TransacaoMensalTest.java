package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransacaoMensalTest {

    @Test
    void construtorDeveArmazenarOMesAlemDosCamposHerdados() {
        TransacaoMensal transacao = new TransacaoMensal(9, "Internet", 100.0, "despesa");

        assertEquals(9, transacao.getMes());
        assertEquals("Internet", transacao.getDescricao());
        assertEquals(100.0, transacao.getValor());
        assertEquals("despesa", transacao.getTipo());
    }

    @Test
    void setMesDeveAtualizarOMes() {
        TransacaoMensal transacao = new TransacaoMensal(1, "Streaming", 40.0, "despesa");

        transacao.setMes(12);

        assertEquals(12, transacao.getMes());
    }

    @Test
    void toStringDeveIncluirOMesAlemDosDadosDaTransacao() {
        TransacaoMensal transacao = new TransacaoMensal(6, "Academia", 120.0, "despesa");

        String texto = transacao.toString();

        assertTrue(texto.contains("Academia"));
        assertTrue(texto.contains("Mes: 6"));
    }

    @Test
    void transacaoMensalDeveSerUmaTransacao() {
        TransacaoMensal transacao = new TransacaoMensal(3, "Seguro", 90.0, "despesa");

        assertTrue(transacao instanceof Transacao);
    }
}
