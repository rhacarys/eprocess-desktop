package br.gov.ac.tce.eprocess.desktop.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario {

	private final StringProperty login;
	private final StringProperty senha;
	private final StringProperty nome;
	private final StringProperty email;
	private final StringProperty crc;

	public Usuario() {
		this(null, null, null, null, null);
	}

	public Usuario(String login, String senha, String nome, String email, String crc) {
		this.login = new SimpleStringProperty(login);
		this.senha = new SimpleStringProperty(senha);
		this.nome = new SimpleStringProperty(nome);
		this.email = new SimpleStringProperty(email);
		this.crc = new SimpleStringProperty(crc);
	}

	public String getSenha() {
		return senha.get();
	}

	public void setSenha(String senha) {
		this.senha.set(senha);
	}

	public StringProperty senhaProperty() {
		return senha;
	}

	public String getLogin() {
		return login.get();
	}

	public void setLogin(String login) {
		this.login.set(login);
	}

	public StringProperty loginProperty() {
		return login;
	}

	public String getNome() {
		return nome.get();
	}

	public void setNome(String nome) {
		this.nome.set(nome);
	}

	public StringProperty nomeProperty() {
		return nome;
	}

	public String getEmail() {
		return email.get();
	}

	public void setEmail(String email) {
		this.email.set(email);
	}

	public StringProperty emailProperty() {
		return email;
	}

	public void setCrc(String crc) {
		this.crc.set(crc);
	}

	public String getCrc() {
		return crc.get();
	}

	public StringProperty crcProperty() {
		return crc;
	}

}
