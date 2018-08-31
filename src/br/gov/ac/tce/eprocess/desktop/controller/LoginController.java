package br.gov.ac.tce.eprocess.desktop.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import br.gov.ac.tce.clienteSeg.Autorizacao;
import br.gov.ac.tce.eprocess.client.ProtocoloInicialClient;
import br.gov.ac.tce.eprocess.desktop.MainApplication;
import br.gov.ac.tce.eprocess.desktop.model.Constantes;
import br.gov.ac.tce.eprocess.desktop.model.Usuario;
import br.gov.ac.tce.ldap.AutenticadorSistemasLDAP;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controlador do Painel de Login
 * 
 * @author Nathaniel Lacerda
 *
 */
public class LoginController extends AnchorPane implements Initializable {

	private MainController main;

	@FXML
	private Pane paneLogin;

	@FXML
	private TextField textLogin;

	@FXML
	private PasswordField passwdLogin;

	private Usuario usuario;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		usuario = new Usuario();

		// Maneira correta de associar as propriedades do modelo à interface: via
		// StringProperty, ObjectProperty etc
		textLogin.textProperty().bindBidirectional(usuario.loginProperty());
		passwdLogin.textProperty().bindBidirectional(usuario.senhaProperty());
	}

	/**
	 * Realiza acessos simultâneos ao web service de cadastro de protocolo e envia
	 * os números de protocolo listados no histórico para serem cadastrados so
	 * Processo Eletrônico
	 * 
	 * @return True se todos os protocolos foram cadastrados com sucesso.
	 * @throws Exception Caso um ou mais números de protocolo não sejam cadastrados,
	 *                   devido a problemas de conexão ou erros no servidor
	 */
	private boolean cadastrarProtocolos() throws Exception {
		boolean sucesso = true;
		Exception exception = null;
		ProtocoloInicialClient client = new ProtocoloInicialClient();
		client.setUrlProcesso(Constantes.URL_PROCESSO);

		Preferences prefs = Preferences.userNodeForPackage(MainApplication.class);
		String registro = prefs.get(Constantes.PREF_PROTOCOLOS, "");
		if (!registro.isEmpty()) {
			for (String protocolo : registro.substring(1).split(",")) {
				try {
					if (client.cadastrarProtocoloInicial(usuario.getLogin(), protocolo)) {
						registro = registro.replace("," + protocolo, "");
					} else {
						sucesso = false;
					}
				} catch (Exception e) {
					exception = e;
					e.printStackTrace();
				}
			}
			if (registro.isEmpty()) {
				prefs.remove(Constantes.PREF_PROTOCOLOS);
			} else {
				prefs.put(Constantes.PREF_PROTOCOLOS, registro);
			}
			if (exception != null)
				throw exception;
		}
		return sucesso;
	}

	@FXML
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

	@FXML
	private void doLogin() {
		main.loadingShow();

		new Thread(() -> {
			try {
				if (autenticar(usuario)) {
					Platform.runLater(() -> {
						try {
							if (cadastrarProtocolos()) {
								showMessage(false, "Os números de protocolo foram cadastrados com sucesso!");
							} else {
								showMessage(true,
										"ATENÇÃO: Já existem protocolos cadastrados para os números que restaram na listagem! ");
							}
							openEtiqueta();
						} catch (IOException e) {
							showMessage(true, "Ocorreu um erro com os arquivos. Favor contatar o suporte.");
						} catch (Exception e) {
							showMessage(true,
									"Ocorreu um erro durante a tentativa de sincronização. Tente novamente mais tarde.");
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
				showMessage(true,
						"Verifique sua conexão com a internet e tente novamente. Caso o problema persista, contate o administrador do Processo Eletrônico.");
			}
		}).start();
	}

	/**
	 * Acessa o serviço de login via LDAP
	 * 
	 * @param usuario Usuário com login e senha válidos
	 * 
	 * @return True se a autenticação foi bem sucedida
	 */
	private boolean autenticar(Usuario usuario) {
		AutenticadorSistemasLDAP autenticador = new AutenticadorSistemasLDAP();
		br.gov.ac.tce.clienteSeg.Usuario usuarioCJUR = autenticador.autentica(usuario.getLogin(), usuario.getSenha(),
				null);
		if (usuarioCJUR != null) {
			if (!temRestricaoEspecificaNoSistema(usuarioCJUR)) {

				if (temPermissaoDeProtolo(usuarioCJUR)) {

					usuario.setNome(usuarioCJUR.getNome());
					usuario.setEmail(usuarioCJUR.getEmail());
					usuario.setCrc(usuarioCJUR.getNumeroCrc());
					return true;
				}

				showMessage(true, "O usuário não tem as permissões necessárias para usar este sistema!");
				return false;
			}
		}

		showMessage(true, "Erro de autenticação! Verifique se as credenciais foram inseridas corretamente.");
		return false;
	}

	private boolean temRestricaoEspecificaNoSistema(br.gov.ac.tce.clienteSeg.Usuario usuarioCJUR) {
		return false;
	}

	private boolean temPermissaoDeProtolo(br.gov.ac.tce.clienteSeg.Usuario usuario) {
		if (usuario != null) {
			for (Autorizacao autorizacao : usuario.getAutorizacoes()) {
				if (autorizacao.getGrupo().equals("protocolo_cadastrar")) {
					return true;
				}
			}
		}
		return true;
	}

	/**
	 * Abre um diálogo na interface com uma mensagem para o usuário
	 * 
	 * @param error Se será uma mensagem de erro, ou apenas informativa
	 * @param msg   A mensagem que será exibida ao usuário
	 */
	private void showMessage(boolean error, String msg) {
		Platform.runLater(() -> {
			Alert alert = new Alert(error ? AlertType.ERROR : AlertType.INFORMATION, msg, ButtonType.CLOSE);
			alert.setTitle("Mensagem");

			alert.setGraphic(null);
			alert.setHeaderText(null);

			DialogPane alertPane = alert.getDialogPane();
			alertPane.getStylesheets().add(MainApplication.class.getResource("view/css/body.css").toExternalForm());
			alertPane.getStyleClass().add("alertEtiqueta");

			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("view/images/icon_sistema.png")));

			alert.setOnCloseRequest(event -> main.loadingHide());
			alert.show();
		});
	}

	public MainController getMain() {
		return main;
	}

	public void setMain(MainController main) {
		this.main = main;
	}
}
