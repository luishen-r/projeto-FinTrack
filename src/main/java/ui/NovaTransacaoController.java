package ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.TransacaoDAO;
import exceptions.EntradaInvalidaException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Transacao;
import model.TransacaoMensal;
import util.Validador;

/**
 * Controller da tela de cadastro (NovaTransacao.fxml). Reaproveita as
 * mesmas regras de validacao (util.Validador) usadas pela versao console
 * (app.Main), evitando duplicacao de logica de negocio.
 */
public class NovaTransacaoController {

    private static final Logger LOGGER = Logger.getLogger(NovaTransacaoController.class.getName());

    @FXML private TextField campoDescricao;
    @FXML private TextField campoValor;
    @FXML private ComboBox<String> comboTipo;
    @FXML private DatePicker seletorData;
    @FXML private CheckBox checkMensal;
    @FXML private ComboBox<Integer> comboMes;

    private TransacaoDAO transacaoDAO;
    private Runnable aoSalvar;

    public void initialize() {
        comboTipo.getItems().addAll("Receita", "Despesa");
        comboTipo.setValue("Receita");

        seletorData.setValue(LocalDate.now());

        for (int mes = 1; mes <= 12; mes++) {
            comboMes.getItems().add(mes);
        }
        comboMes.setValue(LocalDate.now().getMonthValue());
        comboMes.setDisable(true);

        checkMensal.selectedProperty().addListener((obs, valorAntigo, valorNovo) -> comboMes.setDisable(!valorNovo));
    }

    /**
     * Recebe o DAO ja conectado (aberto pela tela principal) e um callback
     * a ser executado apos salvar com sucesso, usado para atualizar a
     * tabela da tela principal.
     */
    public void configurar(TransacaoDAO transacaoDAO, Runnable aoSalvar) {
        this.transacaoDAO = transacaoDAO;
        this.aoSalvar = aoSalvar;
    }

    @FXML
    private void salvar() {
        try {
            String descricao = campoDescricao.getText();
            Validador.validarDescricao(descricao);

            double valor = Double.parseDouble(campoValor.getText().replace(",", "."));
            Validador.validarValor(valor);

            String tipo = comboTipo.getValue();
            Validador.validarTipo(tipo);

            LocalDate data = seletorData.getValue() != null ? seletorData.getValue() : LocalDate.now();

            Transacao transacao;
            if (checkMensal.isSelected()) {
                transacao = new TransacaoMensal(comboMes.getValue(), descricao, valor, tipo, data);
            } else {
                transacao = new Transacao(descricao, valor, tipo, data);
            }

            transacaoDAO.inserir(transacao);

            if (aoSalvar != null) {
                aoSalvar.run();
            }

            fecharJanela();
        } catch (NumberFormatException e) {
            mostrarErro("Informe um valor numérico válido.");
        } catch (EntradaInvalidaException e) {
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao salvar transação", e);
            mostrarErro("Não foi possível salvar a transação: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) campoDescricao.getScene().getWindow();
        stage.close();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR, mensagem);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
