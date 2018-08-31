package br.gov.ac.tce.eprocess.desktop;

import br.gov.ac.tce.eprocess.desktop.controller.EtiquetaController;
import br.gov.ac.tce.eprocess.desktop.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Ponto de partida. Aplicação desktop que permite a utilização da impressora do
 * TCE-AC para impressão das etiquetas do processo eletrônico.
 * 
 * @author Nathaniel Lacerda
 *
 */
public class MainApplication extends Application {

	@Override
	public void start(Stage primaryStage) {

		try {
			// Carrega a janela do sistema (apenas o template com cabeçalho)
			FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("view/Main.fxml"));
			VBox main = mainLoader.load();
			MainController mainController = mainLoader.getController();

			// Carrega o painel principal, para ser adicionado à janela do sistema
			FXMLLoader loaderEtiqueta = new FXMLLoader(getClass().getResource("view/PainelEtiqueta.fxml"));
			AnchorPane etiqueta = loaderEtiqueta.load();
			EtiquetaController etiquetaController = loaderEtiqueta.getController();
			etiquetaController.setMain(mainController);

			mainController.getRoot().getChildren().add(etiqueta);
			Scene scene = new Scene(main);

			// Carrega as configurações da janela do sistema, como ícone e título
			Image applicationIcon = new Image(getClass().getResourceAsStream("view/images/icon_sistema.png"));
			primaryStage.getIcons().add(applicationIcon);
			primaryStage.setTitle("Processo Eletrônico - Protocolo");
			primaryStage.setMinWidth(600);
			primaryStage.setMinHeight(430);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
