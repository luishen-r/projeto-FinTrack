package ui;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.Conexao;
import db.TransacaoDAO;
import db.TransacaoRegistro;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.Formatador;

/**
 * Controller da tela principal (TelaPrincipal.fxml): mostra a tabela de
 * transacoes, o saldo atual, e permite abrir as telas de nova transacao
 * e de relatorio.
 */
public class TelaPrincipalController {

    private static final Logger LOGGER = Logger.getLogger(TelaPrincipalController.class.getName());

    @FXML private TableView<TransacaoRegistro> tabelaTransacoes;
    @FXML private TableColumn<TransacaoRegistro, String> colunaData;
    @FXML private TableColumn<TransacaoRegistro, String> colunaDescricao;
    @FXML private TableColumn<TransacaoRegistro, String> colunaTipo;
    @FXML private TableColumn<TransacaoRegistro, String> colunaValor;
    @FXML private Label lblSaldo;

    private final ObservableList<TransacaoRegistro> dados = FXCollections.observableArrayList();

    private Connection conexao;
    private TransacaoDAO transacaoDAO;

    /**
     * Chamado automaticamente pelo FXMLLoader apos a injecao dos campos
     * anotados com @FXML.
     */
    public void initialize() {
        colunaData.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTransacao().getData().toString()));
        colunaDescricao.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTransacao().getDescricao()));
        colunaTipo.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTransacao().getTipo()));
        colunaValor.setCellValueFactory(cell ->
                new SimpleStringProperty(Formatador.formatarMoeda(cell.getValue().getTransacao().getValor())));

        tabelaTransacoes.setItems(dados);

        try {
            conexao = Conexao.conectar();
            transacaoDAO = new TransacaoDAO(conexao);
            carregarDados();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao conectar ao banco de dados", e);
            mostrarErro("Não foi possível conectar ao banco de dados: " + e.getMessage());
        }
    }

    @FXML
    private void abrirNovaTransacao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NovaTransacao.fxml"));
            Parent root = loader.load();

            NovaTransacaoController controller = loader.getController();
            controller.configurar(transacaoDAO, this::carregarDados);

            Stage stage = new Stage();
            stage.setTitle("Nova Transação");
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Falha ao abrir tela de nova transação", e);
            mostrarErro("Não foi possível abrir a tela de nova transação.");
        }
    }

    @FXML
    private void abrirRelatorio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Relatorio.fxml"));
            Parent root = loader.load();

            RelatorioController controller = loader.getController();
            controller.configurar(transacaoDAO);

            Stage stage = new Stage();
            stage.setTitle("Relatório");
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Falha ao abrir tela de relatório", e);
            mostrarErro("Não foi possível abrir a tela de relatório.");
        }
    }

    @FXML
    private void removerSelecionada() {
        TransacaoRegistro selecionado = tabelaTransacoes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarErro("Selecione uma transação na tabela para remover.");
            return;
        }

        try {
            transacaoDAO.remover(selecionado.getId());
            carregarDados();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao remover transação", e);
            mostrarErro("Não foi possível remover a transação: " + e.getMessage());
        }
    }

    private void carregarDados() {
        try {
            dados.setAll(transacaoDAO.listarTodas());
            lblSaldo.setText("Saldo atual: " + Formatador.formatarMoeda(transacaoDAO.calcularSaldo()));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao carregar transações", e);
            mostrarErro("Não foi possível carregar as transações: " + e.getMessage());
        }
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR, mensagem);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Fecha a conexao com o banco de dados. Deve ser chamado quando a
     * janela principal for fechada (ver app.FinApp).
     */
    public void encerrar() {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Falha ao fechar conexão com o banco de dados", e);
            }
        }
    }
}
