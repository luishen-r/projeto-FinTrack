package repository;

import model.Transacao;
import model.TransacaoMensal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioGenericoTest {

    private RepositorioGenerico<Transacao> repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioGenerico<>();
    }

    @Test
    void repositorioDeveComecarVazio() {
        assertTrue(repositorio.estaVazio());
        assertEquals(0, repositorio.total());
    }

    @Test
    void adicionarDeveIncluirElementoNoRepositorio() {
        Transacao transacao = new Transacao("Salario", 1000.0, "receita");

        repositorio.adicionar(transacao);

        assertEquals(1, repositorio.total());
        assertEquals(transacao, repositorio.obter(0));
    }

    @Test
    void adicionarTodosDeveAceitarColecaoDeSubtipo() {
        // Testa o wildcard "? extends T": uma List<TransacaoMensal> deve
        // poder ser adicionada a um RepositorioGenerico<Transacao>.
        List<TransacaoMensal> mensais = new ArrayList<>();
        mensais.add(new TransacaoMensal(1, "Internet", 100.0, "despesa"));
        mensais.add(new TransacaoMensal(2, "Streaming", 40.0, "despesa"));

        repositorio.adicionarTodos(mensais);

        assertEquals(2, repositorio.total());
    }

    @Test
    void copiarParaDeveAceitarColecaoDeSupertipo() {
        // Testa o wildcard "? super T": um RepositorioGenerico<TransacaoMensal>
        // deve poder copiar seus elementos para uma List<Transacao>.
        RepositorioGenerico<TransacaoMensal> repositorioMensal = new RepositorioGenerico<>();
        repositorioMensal.adicionar(new TransacaoMensal(3, "Seguro", 90.0, "despesa"));

        List<Transacao> destino = new ArrayList<>();
        repositorioMensal.copiarPara(destino);

        assertEquals(1, destino.size());
    }

    @Test
    void removerDeveExcluirElementoExistente() {
        Transacao transacao = new Transacao("Mercado", 300.0, "despesa");
        repositorio.adicionar(transacao);

        boolean removido = repositorio.remover(transacao);

        assertTrue(removido);
        assertTrue(repositorio.estaVazio());
    }

    @Test
    void removerElementoInexistenteDeveRetornarFalse() {
        Transacao transacao = new Transacao("Mercado", 300.0, "despesa");

        boolean removido = repositorio.remover(transacao);

        assertFalse(removido);
    }

    @Test
    void removerPorIndiceDeveRetornarOElementoRemovido() {
        Transacao transacao1 = new Transacao("Salario", 1500.0, "receita");
        Transacao transacao2 = new Transacao("Aluguel", 800.0, "despesa");
        repositorio.adicionar(transacao1);
        repositorio.adicionar(transacao2);

        Transacao removido = repositorio.removerPorIndice(0);

        assertEquals(transacao1, removido);
        assertEquals(1, repositorio.total());
    }

    @Test
    void listarTodosDeveRetornarCopiaIndependente() {
        repositorio.adicionar(new Transacao("Salario", 1500.0, "receita"));

        List<Transacao> copia = repositorio.listarTodos();
        copia.clear();

        // Alterar a copia nao deve afetar o repositorio original.
        assertEquals(1, repositorio.total());
    }
}
