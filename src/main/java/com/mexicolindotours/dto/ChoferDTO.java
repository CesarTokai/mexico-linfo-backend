package com.mexicolindotours.dto;

import java.time.LocalDate;

public class ChoferDTO {

	private Long id;
	private String nombre;
	private String telefono;
	private LocalDate licenciaVencimiento;
	private Boolean activo;

	public ChoferDTO() {
	}

	public ChoferDTO(Long id, String nombre, String telefono, LocalDate licenciaVencimiento, Boolean activo) {
		this.id = id;
		this.nombre = nombre;
		this.telefono = telefono;
		this.licenciaVencimiento = licenciaVencimiento;
		this.activo = activo;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }
	public LocalDate getLicenciaVencimiento() { return licenciaVencimiento; }
	public void setLicenciaVencimiento(LocalDate licenciaVencimiento) { this.licenciaVencimiento = licenciaVencimiento; }
	public Boolean getActivo() { return activo; }
	public void setActivo(Boolean activo) { this.activo = activo; }

}
