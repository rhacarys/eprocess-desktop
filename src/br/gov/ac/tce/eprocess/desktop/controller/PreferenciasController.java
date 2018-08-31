package br.gov.ac.tce.eprocess.desktop.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import br.gov.ac.tce.eprocess.desktop.MainApplication;
import br.gov.ac.tce.eprocess.desktop.model.Constantes;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Controlador do Painel de Etiqueta
 * 
 * @author Nathaniel Lacerda
 *
 */
public class PreferenciasController extends AnchorPane implements Initializable {

	private MainController main;

	@FXML
	private TextField nomeImpressora;

	@FXML
	private TextField urlProcesso;

	private Preferences prefs;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		prefs = Preferences.userNodeForPackage(MainApplication.class);
		nomeImpressora.setText(prefs.get(Constantes.PREF_PRINTER, ""));
		
		urlProcesso.setText(Constantes.URL_PROCESSO.replace("http://", ""));
	}

	@FXML
	private void salvar() {
		if (!nomeImpressora.getText().trim().isEmpty()) {
			prefs.put(Constantes.PREF_PRINTER, nomeImpressora.getText().trim());
		}
		openEtiqueta();
	}

	@FXML
	private void cancelar() {
		openEtiqueta();
	}

	private void openEtiqueta() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("view/PainelEtiqueta.fxml"));
			main.getRoot().getChildren().setAll((AnchorPane) loader.load());

			EtiquetaController etiquetaController = loader.getController();
			etiquetaController.setMain(main);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MainController getMain() {
		return main;
	}

	public void setMain(MainController main) {
		this.main = main;
	}
}
