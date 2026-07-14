package com.mexicolindotours.dto;

import java.time.LocalDate;

public class DisponibilidadChoferDTO {

	private Long id;
	private Long choferId;
	private LocalDate fecha;
	private Boolean disponible;
	private String notas;

	public DisponibilidadChoferDTO() {
	}

	public DisponibilidadChoferDTO(Long id, Long choferId, LocalDate fecha, Boolean disponible, String notas) {
		this.id = id;
		this.choferId = choferId;
		this.fecha = fecha;
		this.disponible = disponible;
		this.notas = notas;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Long getChoferId() { return choferId; }
	public void setChoferId(Long choferId) { this.choferId = choferId; }
	public LocalDate getFecha() { return fecha; }
	public void setFecha(LocalDate fecha) { this.fecha = fecha; }
	public Boolean getDisponible() { return disponible; }
	public void setDisponible(Boolean disponible) { this.disponible = disponible; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }

}
