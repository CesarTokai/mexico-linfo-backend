package com.mexicolindotours.dto;

public class RegistroPublicoRequest {

	private String nombre;
	private String correo;
	private String password;
	private String telefono;

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getCorreo() { return correo; }
	public void setCorreo(String correo) { this.correo = correo; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }

}
