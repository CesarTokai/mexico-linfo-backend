package com.mexicolindotours.dto;

public class LoginPublicoResponse {

	private String token;
	private Long id;
	private String nombre;
	private String correo;

	public LoginPublicoResponse(String token, Long id, String nombre, String correo) {
		this.token = token;
		this.id = id;
		this.nombre = nombre;
		this.correo = correo;
	}

	public String getToken() { return token; }
	public Long getId() { return id; }
	public String getNombre() { return nombre; }
	public String getCorreo() { return correo; }

}
