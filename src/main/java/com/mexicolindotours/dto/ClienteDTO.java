package com.mexicolindotours.dto;

public class ClienteDTO {

	private Long id;
	private String nombre;
	private String telefono;
	private String notas;

	public ClienteDTO() {
	}

	public ClienteDTO(Long id, String nombre, String telefono, String notas) {
		this.id = id;
		this.nombre = nombre;
		this.telefono = telefono;
		this.notas = notas;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }

}
