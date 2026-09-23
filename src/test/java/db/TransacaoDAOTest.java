package db;

import model.Transacao;
import model.TransacaoMensal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes do TransacaoDAO usando SQLite em memoria. Nao depende de um
 * servidor MySQL rodando: a conexao e o schema (compativel com SQLite)
 * sao criados aqui mesmo, exclusivamente para os testes. Em produção
 * (ver db.Conexao) o banco continua sendo o MySQL.
 */
class TransacaoDAOTest {

    private Connection conexao;
    private TransacaoDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        conexao = DriverManager.getConnection("jdbc:sqlite::memory:");
        criarTabela();
        dao = new TransacaoDAO(conexao);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conexao != null) {
            conexao.close();
        }
    }

    private void criarTabela() throws SQLException {
        String sql = """
                CREATE TABLE transacoes (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    descricao TEXT    NOT NULL,
                    valor     REAL    NOT NULL,
                    tipo      TEXT    NOT NULL,
                    data      TEXT    NOT NULL,
                    mensal    INTEGER NOT NULL DEFAULT 0,
                    mes       INTEGER
                )
                """;
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Test
    void inserirDeveGerarUmIdMaiorQueZero() throws SQLException {
        Transacao transacao = new Transacao("Salario", 2000.0, "receita", LocalDate.of(2026, 9, 1));

        TransacaoRegistro registro = dao.inserir(transacao);

        assertTrue(registro.getId() > 0);
    }

    @Test
    void listarTodasDeveRetornarTransacoesInseridas() throws SQLException {
        dao.inserir(new Transacao("Salario", 2000.0, "receita", LocalDate.of(2026, 9, 1)));
        dao.inserir(new Transacao("Aluguel", 800.0, "despesa", LocalDate.of(2026, 9, 5)));

        List<TransacaoRegistro> registros = dao.listarTodas();

        assertEquals(2, registros.size());
    }

    @Test
    void listarTodasDeveReconstruirTransacaoMensalCorretamente() throws SQLException {
        dao.inserir(new TransacaoMensal(9, "Internet", 100.0, "despesa", LocalDate.of(2026, 9, 10)));

        List<TransacaoRegistro> registros = dao.listarTodas();

        assertEquals(1, registros.size());
        Transacao transacao = registros.get(0).getTransacao();
        assertTrue(transacao instanceof TransacaoMensal);
        assertEquals(9, ((TransacaoMensal) transacao).getMes());
    }

    @Test
    void calcularSaldoDeveSomarReceitasESubtrairDespesas() throws SQLException {
        dao.inserir(new Transacao("Salario", 2000.0, "receita", LocalDate.of(2026, 9, 1)));
        dao.inserir(new Transacao("Aluguel", 800.0, "despesa", LocalDate.of(2026, 9, 5)));

        double saldo = dao.calcularSaldo();

        assertEquals(1200.0, saldo, 0.0001);
    }

    @Test
    void calcularSaldoSemTransacoesDeveSerZero() throws SQLException {
        assertEquals(0.0, dao.calcularSaldo(), 0.0001);
    }

    @Test
    void removerDeveExcluirATransacaoDoBanco() throws SQLException {
        TransacaoRegistro registro = dao.inserir(new Transacao("Mercado", 300.0, "despesa", LocalDate.of(2026, 9, 8)));

        dao.remover(registro.getId());

        List<TransacaoRegistro> registros = dao.listarTodas();
        assertTrue(registros.isEmpty());
    }

    @Test
    void buscarPorIdDeveRetornarNuloQuandoNaoExiste() throws SQLException {
        TransacaoRegistro registro = dao.buscarPorId(999);

        assertNull(registro);
    }

    @Test
    void buscarPorIdDeveRetornarATransacaoCorreta() throws SQLException {
        TransacaoRegistro inserido = dao.inserir(new Transacao("Freelance", 500.0, "receita", LocalDate.of(2026, 9, 12)));

        TransacaoRegistro encontrado = dao.buscarPorId(inserido.getId());

        assertNotNull(encontrado);
        assertEquals("Freelance", encontrado.getTransacao().getDescricao());
    }

    @Test
    void inserirEmLoteDeveGravarTodasAsTransacoes() throws SQLException {
        List<Transacao> transacoes = List.of(
                new Transacao("Salario", 2000.0, "receita", LocalDate.of(2026, 9, 1)),
                new Transacao("Aluguel", 800.0, "despesa", LocalDate.of(2026, 9, 5)),
                new Transacao("Mercado", 300.0, "despesa", LocalDate.of(2026, 9, 8))
        );

        List<TransacaoRegistro> registros = dao.inserirEmLote(transacoes);

        assertEquals(3, registros.size());
        assertEquals(3, dao.listarTodas().size());
    }
}
