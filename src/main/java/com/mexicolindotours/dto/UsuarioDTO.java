package com.mexicolindotours.dto;

public class UsuarioDTO {

	private Long id;
	private String nombre;
	private String correo;
	private String rol;
	private Boolean activo;

	public UsuarioDTO() {
	}

	public UsuarioDTO(Long id, String nombre, String correo, String rol, Boolean activo) {
		this.id = id;
		this.nombre = nombre;
		this.correo = correo;
		this.rol = rol;
		this.activo = activo;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getCorreo() { return correo; }
	public void setCorreo(String correo) { this.correo = correo; }
	public String getRol() { return rol; }
	public void setRol(String rol) { this.rol = rol; }
	public Boolean getActivo() { return activo; }
	public void setActivo(Boolean activo) { this.activo = activo; }

}
