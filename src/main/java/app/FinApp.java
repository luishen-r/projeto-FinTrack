package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.TelaPrincipalController;

/**
 * A interface é construida a partir de arquivos FXML (editaveis no Scene
 * Builder) e estilizada com um arquivo CSS. Para executar esta versao:
 * mvn javafx:run. ATENÇÃO: Siga o passo a passo no readme.md para conseguir executar e aproveitar
 * o programa da melhor forma! :D
 */
public class FinApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TelaPrincipal.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setTitle("FinTrack - Gestão de Finanças Pessoais");
        stage.setScene(scene);

        TelaPrincipalController controller = loader.getController();
        stage.setOnCloseRequest(event -> controller.encerrar());

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
