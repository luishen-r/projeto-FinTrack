package controller;

import model.Transacao;
import model.TransacaoMensal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinTrackerTest {

    private FinTracker finTracker;

    @BeforeEach
    void setUp() {
        finTracker = new FinTracker();
    }

    @Test
    void saldoDeveSerZeroQuandoNaoHaTransacoes() {
        assertEquals(0.0, finTracker.calcularSaldo());
    }

    @Test
    void receitaDeveAumentarOSaldo() {
        finTracker.addTransacao(new Transacao("Salario", 1000.0, "receita"));

        assertEquals(1000.0, finTracker.calcularSaldo());
    }

    @Test
    void despesaDeveDiminuirOSaldo() {
        finTracker.addTransacao(new Transacao("Aluguel", 800.0, "despesa"));

        assertEquals(-800.0, finTracker.calcularSaldo());
    }

    @Test
    void saldoDeveConsiderarReceitasEDespesasJuntas() {
        finTracker.addTransacao(new Transacao("Salario", 2000.0, "receita"));
        finTracker.addTransacao(new Transacao("Aluguel", 800.0, "despesa"));
        finTracker.addTransacao(new TransacaoMensal(9, "Internet", 100.0, "despesa"));

        assertEquals(1100.0, finTracker.calcularSaldo(), 0.0001);
    }

    @Test
    void tipoDeveSerTratadoDeFormaCaseInsensitive() {
        finTracker.addTransacao(new Transacao("Freelance", 500.0, "RECEITA"));

        assertEquals(500.0, finTracker.calcularSaldo());
    }

    @Test
    void removerTransacaoDeveExcluirDoCalculoDoSaldo() {
        finTracker.addTransacao(new Transacao("Salario", 1500.0, "receita"));
        finTracker.addTransacao(new Transacao("Mercado", 300.0, "despesa"));

        finTracker.removerTransacao(1); // remove "Mercado"

        assertEquals(1500.0, finTracker.calcularSaldo());
    }

    @Test
    void removerTransacaoComIndiceInvalidoNaoDeveAlterarSaldo() {
        finTracker.addTransacao(new Transacao("Salario", 1500.0, "receita"));

        finTracker.removerTransacao(99); // indice fora do intervalo, deve ser ignorado
        finTracker.removerTransacao(-1); // indice negativo, deve ser ignorado

        assertEquals(1500.0, finTracker.calcularSaldo());
    }
}
