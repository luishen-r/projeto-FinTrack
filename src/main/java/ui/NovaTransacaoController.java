package ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.TransacaoDAO;
import db.TransacaoRegistro;
import exceptions.EntradaInvalidaException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Transacao;
import model.TransacaoMensal;
import util.Validador;

public class NovaTransacaoController {

    private static final Logger LOGGER = Logger.getLogger(NovaTransacaoController.class.getName());

    @FXML private Label lblTitulo;
    @FXML private Button btnSalvar;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoValor;
    @FXML private ComboBox<String> comboTipo;
    @FXML private DatePicker seletorData;
    @FXML private CheckBox checkMensal;
    @FXML private ComboBox<Integer> comboMes;

    private TransacaoDAO transacaoDAO;
    private Runnable aoSalvar;
    private TransacaoRegistro registroEmEdicao;

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

    public void configurar(TransacaoDAO transacaoDAO, Runnable aoSalvar) {
        this.transacaoDAO = transacaoDAO;
        this.aoSalvar = aoSalvar;
        this.registroEmEdicao = null;
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

            if (registroEmEdicao != null) {
                transacaoDAO.atualizar(transacao, registroEmEdicao.getId());
            } else {
                transacaoDAO.inserir(transacao);
            }

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

    public void configurarParaEdicao(TransacaoDAO transacaoDAO, Runnable aoSalvar, TransacaoRegistro registro) {
        this.registroEmEdicao = registro;
        this.aoSalvar = aoSalvar;
        this.transacaoDAO = transacaoDAO;

        Transacao transacao = registro.getTransacao();

        campoDescricao.setText(transacao.getDescricao());
        campoValor.setText(String.valueOf(transacao.getValor()));
        comboTipo.setValue(capitalizar(transacao.getTipo()));
        seletorData.setValue(transacao.getData());

        if (transacao instanceof TransacaoMensal transacaoMensal) {
            checkMensal.setSelected(true);
            comboMes.setDisable(false);
            comboMes.setValue(transacaoMensal.getMes());
        } else {
            checkMensal.setSelected(false);
            comboMes.setDisable(true);
        }

        lblTitulo.setText("Editar Transação");
        btnSalvar.setText("Atualizar");
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

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}
