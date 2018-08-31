package br.gov.ac.tce.eprocess.desktop.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import br.gov.ac.tce.eprocess.desktop.MainApplication;
import br.gov.ac.tce.eprocess.desktop.model.Constantes;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controlador do Painel de Etiqueta
 * 
 * @author Nathaniel Lacerda
 *
 */
public class EtiquetaController extends AnchorPane implements Initializable {

	@FXML
	private AnchorPane paneEtiqueta;

	@FXML
	private ListView<String> protocolos;

	private MainController main;

	private String protocolo;

	private Preferences prefs;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		protocolos.setCellFactory(lv -> {

			ListCell<String> cell = new ListCell<>();

			// Criação do menu de contexto, para realizar operações básicas na listagem de
			// protocolo
			ContextMenu contextMenu = new ContextMenu();

			MenuItem copyItem = new MenuItem();
			copyItem.textProperty().bind(Bindings.format("Copiar \"%s\"", cell.itemProperty()));
			copyItem.setOnAction(event -> copyToClipboard(cell.getItem()));

			MenuItem deleteItem = new MenuItem();
			deleteItem.textProperty().bind(Bindings.format("Copiar e DELETAR"));
			deleteItem.setOnAction(event -> {
				copyToClipboard(cell.getItem());

				prefs = Preferences.userNodeForPackage(MainApplication.class);
				String registro = prefs.get(Constantes.PREF_PROTOCOLOS, "");
				registro = registro.replace("," + cell.getItem(), "");
				if (registro.isEmpty()) {
					prefs.remove(Constantes.PREF_PROTOCOLOS);
				} else {
					prefs.put(Constantes.PREF_PROTOCOLOS, registro);
				}
				protocolos.getItems().remove(cell.getItem());
			});

			MenuItem addItem = new MenuItem();
			addItem.textProperty().bind(Bindings.format("Adicinar o protocolo copiado à lista"));
			addItem.setOnAction(event -> {
				String protocolo = copyFromClipboard();
				try {
					Long.parseLong(protocolo);
					String registro = prefs.get(Constantes.PREF_PROTOCOLOS, "");
					if (!registro.contains(protocolo)) {
						prefs.put(Constantes.PREF_PROTOCOLOS, registro + "," + protocolo);
						protocolos.getItems().add(protocolo);
					}
				} catch (Exception e) {
					System.out.println("O valor \"" + protocolo + "\" não é um número de protocolo válido.");
				}
			});

			contextMenu.getItems().addAll(copyItem, deleteItem, addItem);
			cell.textProperty().bind(cell.itemProperty());
			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
				if (isNowEmpty) {
					cell.setContextMenu(null);
				} else {
					cell.setContextMenu(contextMenu);
				}
			});
			return cell;
		});

		prefs = Preferences.userNodeForPackage(MainApplication.class);
		String registro = prefs.get(Constantes.PREF_PROTOCOLOS, "");
		if (!registro.isEmpty()) {
			protocolos.getItems().addAll(registro.substring(1).split(","));
		}
	}

	@FXML
	private void openLogin() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApplication.class.getResource("view/PainelLogin.fxml"));
			main.getRoot().getChildren().setAll((AnchorPane) loader.load());

			LoginController loginController = loader.getController();
			loginController.setMain(main);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void doEtiqueta() {

		protocolo = gerarProtocolo();

		Alert alert = criarDialogoConfirmacao(protocolo);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			showAlertaImprimindo();

			Platform.runLater(() -> {
				try {
					imprimirEtiqueta(protocolo);
				} catch (IOException e) {
					e.printStackTrace();
					showAlertaErro(e.getMessage());
				}				
			});
		}
	}

	/**
	 * Cria um diálogo com o número de protocolo gerado, para que o usuário confirme
	 * se deseja imprimir a etiqueta
	 * 
	 * @param protocolo Número de protocolo para exibição
	 * 
	 * @return O Alerta
	 */
	private Alert criarDialogoConfirmacao(String protocolo) {
		Alert alert = new Alert(AlertType.CONFIRMATION,
				"Deseja imprimir a etiqueta para o protocolo de número " + protocolo + "?", ButtonType.OK,
				ButtonType.CANCEL);

		ImageView graphic = new ImageView(
				new Image(MainApplication.class.getResourceAsStream("view/images/barcode.png")));
		graphic.setFitHeight(50);
		alert.setGraphic(graphic);
		alert.setTitle("Processo Eletrônico - Protocolo");
		alert.setHeaderText("Imprimir Etiqueta");

		DialogPane alertPane = alert.getDialogPane();
		alertPane.getStylesheets().add(MainApplication.class.getResource("view/css/body.css").toExternalForm());
		alertPane.getStyleClass().add("alertEtiqueta");

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("view/images/icon_sistema.png")));
		return alert;
	}

	/**
	 * Abre um alerta informando que a etiqueta está sendo enviada para impressão
	 */
	private void showAlertaImprimindo() {
		Alert alert = new Alert(AlertType.INFORMATION,
				"Uma janela do seu navegador padrão será aberta para a impressão da etiqueta. Em caso de sucesso, ela fechará automaticamente.");

		ImageView graphic = new ImageView(
				new Image(MainApplication.class.getResourceAsStream("view/images/printer.png")));
		graphic.setFitHeight(60);
		graphic.setFitHeight(60);
		alert.setGraphic(graphic);
		alert.setTitle("Processo Eletrônico - Protocolo");
		alert.setHeaderText("Imprimindo...");

		DialogPane alertPane = alert.getDialogPane();
		alertPane.getStylesheets().add(MainApplication.class.getResource("view/css/body.css").toExternalForm());
		alertPane.getStyleClass().add("alertEtiqueta");

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("view/images/icon_sistema.png")));

		alert.show();
	}

	/**
	 * Cria o arquivo html temporário com os dados de impressão, e chama o browser
	 * para realizar tal operação
	 * 
	 * @param protocolo Número do protocolo que será impresso na etiqueta
	 * 
	 * @throws IOException Caso haja algum erro ao criar o arquivo temporário, ou ao
	 *                     chamar o navegador para abrí-lo
	 */
	private void imprimirEtiqueta(String protocolo) throws IOException {
		File file = new File("web/print.html");
		if (!file.exists()) {
			file = new File("../web/print.html");
		}
		Path path = file.toPath().toAbsolutePath();
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll("numero = '\\d*';", "numero = '" + protocolo + "';")
				.replaceAll("printer = '[ \\w]*';", "printer = '" + prefs.get(Constantes.PREF_PRINTER, "") + "';")
				.replaceAll("WEBFOLDER", path.getParent().toUri().toString());

		File output = File.createTempFile("imprimir_etiqueta", ".html");
		Path outputPath = output.toPath().toAbsolutePath();
		Files.write(outputPath, content.getBytes(charset));

		Desktop.getDesktop().browse(outputPath.toUri());

		String registro = prefs.get(Constantes.PREF_PROTOCOLOS, "");
		prefs.put(Constantes.PREF_PROTOCOLOS, registro + "," + protocolo);

		protocolos.getItems().add(protocolo);
	}

	/**
	 * Abre um diálogo na interface com uma mensagem de erro para o usuário
	 * 
	 * @param message A mensagem a ser exibida
	 */
	private void showAlertaErro(String message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR, "Ocorreu um erro durante a tentativa de impressão da Etiqueta. \n(" + message + ")",
					ButtonType.CLOSE);
			alert.setTitle("Processo Eletrônico - Protocolo");

			alert.setHeaderText("Alerta!");

			DialogPane alertPane = alert.getDialogPane();
			alertPane.getStylesheets().add(MainApplication.class.getResource("view/css/body.css").toExternalForm());
			alertPane.getStyleClass().add("alertEtiqueta");

			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("view/images/icon_sistema.png")));

			alert.setOnCloseRequest(event -> main.loadingHide());
			alert.show();
		});
	}

	private String gerarProtocolo() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * Copia o texto passado como parâmetro para a área de transferência do sistema
	 * operacional
	 * 
	 * @param texto String à ser copiada
	 */
	private void copyToClipboard(String texto) {
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
		content.putString(texto);
		clipboard.setContent(content);
	}

	/**
	 * Recupera o atual conteúdo da área de transferência do sistema operacional, em
	 * formato de texto
	 * 
	 * @return O conteúdo do clipboard
	 */
	private String copyFromClipboard() {
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		return clipboard.getString();
	}

	public MainController getMain() {
		return main;
	}

	public void setMain(MainController main) {
		this.main = main;
	}

}
