package ui;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.TransacaoDAO;
import db.TransacaoRegistro;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import util.Formatador;

/**
 * Controller da tela de relatorio (Relatorio.fxml): mostra o total de
 * receitas, total de despesas, saldo e quantidade de transacoes.
 */
public class RelatorioController {

    private static final Logger LOGGER = Logger.getLogger(RelatorioController.class.getName());

    @FXML private Label lblSaldo;
    @FXML private Label lblTotalReceitas;
    @FXML private Label lblTotalDespesas;
    @FXML private Label lblQuantidade;

    private TransacaoDAO transacaoDAO;

    public void configurar(TransacaoDAO transacaoDAO) {
        this.transacaoDAO = transacaoDAO;
        carregarRelatorio();
    }

    private void carregarRelatorio() {
        try {
            List<TransacaoRegistro> registros = transacaoDAO.listarTodas();

            double totalReceitas = registros.stream()
                    .map(TransacaoRegistro::getTransacao)
                    .filter(t -> t.getTipo().equalsIgnoreCase("receita"))
                    .mapToDouble(t -> t.getValor())
                    .sum();

            double totalDespesas = registros.stream()
                    .map(TransacaoRegistro::getTransacao)
                    .filter(t -> t.getTipo().equalsIgnoreCase("despesa"))
                    .mapToDouble(t -> t.getValor())
                    .sum();

            lblTotalReceitas.setText("Total de receitas: " + Formatador.formatarMoeda(totalReceitas));
            lblTotalDespesas.setText("Total de despesas: " + Formatador.formatarMoeda(totalDespesas));
            lblSaldo.setText("Saldo atual: " + Formatador.formatarMoeda(totalReceitas - totalDespesas));
            lblQuantidade.setText("Total de transações: " + registros.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao carregar relatório", e);
            mostrarErro("Não foi possível carregar o relatório: " + e.getMessage());
        }
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR, mensagem);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
