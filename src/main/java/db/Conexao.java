package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responsavel por centralizar o acesso ao banco de dados MySQL
 * e garantir que a tabela "transacoes" exista antes do uso.
 * Pre-requisitos:
 *   - Um servidor MySQL em execucao (local ou remoto).
 *   - Um banco de dados chamado "fintrack" ja criado, por exemplo:
 *       CREATE DATABASE fintrack CHARACTER SET utf8mb4;
 *
 * Ajuste HOST, PORTA, BANCO, USUARIO e SENHA conforme o seu ambiente.
 */
public class Conexao {

    private static final Logger LOGGER = Logger.getLogger(Conexao.class.getName());

    private static final String HOST = "localhost";
    private static final String PORTA = "3306";
    private static final String BANCO = "fintrack";

    private static final String USUARIO = "root";
    private static final String SENHA = "senha";

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORTA + "/" + BANCO
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private Conexao() {
        // classe utilitaria, nao deve ser instanciada
    }

    /**
     * Abre uma nova conexao com o banco MySQL e garante que a tabela
     * "transacoes" exista.
     */
    public static Connection conectar() throws SQLException {
        try {
            Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            criarTabelaSeNaoExistir(conexao);
            return conexao;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao conectar ao banco de dados MySQL", e);
            throw e;
        }
    }

    private static void criarTabelaSeNaoExistir(Connection conexao) throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS transacoes (
                    id         INT AUTO_INCREMENT PRIMARY KEY,
                    descricao  VARCHAR(255)  NOT NULL,
                    valor      DECIMAL(10,2) NOT NULL,
                    tipo       VARCHAR(20)   NOT NULL,
                    data       DATE          NOT NULL,
                    mensal     TINYINT(1)    NOT NULL DEFAULT 0,
                    mes        INT
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;

        try (Statement statement = conexao.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao criar a tabela 'transacoes'", e);
            throw e;
        }
    }
}
