package br.gov.ac.tce.eprocess.desktop.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import br.gov.ac.tce.eprocess.desktop.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Controlador da Janela Global da aplicação (com cabeçalho)
 * 
 * @author Nathaniel Lacerda
 *
 */
public class MainController extends VBox implements Initializable {

	@FXML
	private Pane root;

	@FXML
	private Pane loadingPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	private void close() {
		System.exit(0);
	}

	@FXML
	private void openPreferencias() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("view/PainelPreferencias.fxml"));
			getRoot().getChildren().setAll((AnchorPane) loader.load());

			PreferenciasController preferenciasController = loader.getController();
			preferenciasController.setMain(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Pane getRoot() {
		return root;
	}

	public Pane getLoadingPane() {
		return loadingPane;
	}

	public void loadingShow() {
		root.setDisable(true);
		loadingPane.setVisible(true);

		requestParentLayout();
	}

	public void loadingHide() {
		root.setDisable(false);
		loadingPane.setVisible(false);
	}

}
