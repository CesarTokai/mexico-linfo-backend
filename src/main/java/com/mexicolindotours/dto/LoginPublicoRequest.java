package com.mexicolindotours.dto;

/** Login del cliente: acepta correo O telefono, lo que haya usado al registrarse. */
public class LoginPublicoRequest {

	private String identificador;
	private String password;

	public String getIdentificador() { return identificador; }
	public void setIdentificador(String identificador) { this.identificador = identificador; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

}
