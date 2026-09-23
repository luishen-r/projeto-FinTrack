package db;

import model.Transacao;
import model.TransacaoMensal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object responsavel por persistir e recuperar {@link Transacao}
 * (e suas subclasses, como {@link TransacaoMensal}) usando JDBC puro.
 * A classe NAO cria a tabela "transacoes" sozinha: quem abre a conexao
 * (Conexao, em produção, ou o proprio teste) e quem garante que o schema
 * exista, ja que a sintaxe de criacao de tabela muda entre bancos.
 */
public class TransacaoDAO {

    private static final Logger LOGGER = Logger.getLogger(TransacaoDAO.class.getName());

    private final Connection conexao;

    public TransacaoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * Insere uma transacao no banco e retorna o registro ja com o id gerado.
     */
    public TransacaoRegistro inserir(Transacao transacao) throws SQLException {
        String sql = "INSERT INTO transacoes (descricao, valor, tipo, data, mensal, mes) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(stmt, transacao);
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                int id = chaves.next() ? chaves.getInt(1) : -1;
                return new TransacaoRegistro(id, transacao);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao inserir transacao: " + transacao.getDescricao(), e);
            throw e;
        }
    }

    /**
     * Insere varias transacoes em uma unica transacao de banco de dados
     * (commit/rollback), garantindo que ou todas sejam gravadas, ou nenhuma.
     */
    public List<TransacaoRegistro> inserirEmLote(List<Transacao> transacoes) throws SQLException {
        List<TransacaoRegistro> registros = new ArrayList<>();
        boolean autoCommitOriginal = conexao.getAutoCommit();

        try {
            conexao.setAutoCommit(false);

            for (Transacao transacao : transacoes) {
                registros.add(inserir(transacao));
            }

            conexao.commit();
            return registros;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao inserir lote de transacoes, revertendo (rollback)", e);
            conexao.rollback();
            throw e;
        } finally {
            conexao.setAutoCommit(autoCommitOriginal);
        }
    }

    /**
     * Retorna todas as transacoes cadastradas, ja reconstruidas como
     * Transacao ou TransacaoMensal, conforme o que foi salvo.
     */
    public List<TransacaoRegistro> listarTodas() throws SQLException {
        String sql = "SELECT id, descricao, valor, tipo, data, mensal, mes FROM transacoes ORDER BY id";
        List<TransacaoRegistro> registros = new ArrayList<>();

        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                registros.add(mapearRegistro(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao listar transacoes", e);
            throw e;
        }

        return registros;
    }

    /**
     * Busca uma transacao pelo seu id.
     */
    public TransacaoRegistro buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, descricao, valor, tipo, data, mensal, mes FROM transacoes WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapearRegistro(rs) : null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao buscar transacao por id: " + id, e);
            throw e;
        }
    }

    /**
     * Calcula o saldo (receitas - despesas) diretamente no banco de dados.
     */
    public double calcularSaldo() throws SQLException {
        String sql = """
                SELECT
                    COALESCE(SUM(CASE WHEN LOWER(tipo) = 'receita' THEN valor ELSE 0 END), 0) -
                    COALESCE(SUM(CASE WHEN LOWER(tipo) = 'despesa' THEN valor ELSE 0 END), 0) AS saldo
                FROM transacoes
                """;

        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getDouble("saldo") : 0.0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao calcular saldo", e);
            throw e;
        }
    }

    /**
     * Remove uma transacao pelo seu id.
     */
    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM transacoes WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao remover transacao id: " + id, e);
            throw e;
        }
    }

    private void preencherParametros(PreparedStatement stmt, Transacao transacao) throws SQLException {
        boolean isMensal = transacao instanceof TransacaoMensal;

        stmt.setString(1, transacao.getDescricao());
        stmt.setDouble(2, transacao.getValor());
        stmt.setString(3, transacao.getTipo());
        stmt.setDate(4, Date.valueOf(transacao.getData()));
        stmt.setInt(5, isMensal ? 1 : 0);

        if (isMensal) {
            stmt.setInt(6, ((TransacaoMensal) transacao).getMes());
        } else {
            stmt.setNull(6, java.sql.Types.INTEGER);
        }
    }

    private TransacaoRegistro mapearRegistro(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String descricao = rs.getString("descricao");
        double valor = rs.getDouble("valor");
        String tipo = rs.getString("tipo");
        LocalDate data = rs.getDate("data").toLocalDate();
        boolean mensal = rs.getInt("mensal") == 1;

        Transacao transacao;
        if (mensal) {
            int mes = rs.getInt("mes");
            transacao = new TransacaoMensal(mes, descricao, valor, tipo, data);
        } else {
            transacao = new Transacao(descricao, valor, tipo, data);
        }

        return new TransacaoRegistro(id, transacao);
    }
}
