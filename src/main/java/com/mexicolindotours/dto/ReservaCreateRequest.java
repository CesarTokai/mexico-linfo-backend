package com.mexicolindotours.dto;

public class ReservaCreateRequest {

	private Long salidaId;
	private Integer numAsientos;
	private String notas;

	public Long getSalidaId() { return salidaId; }
	public void setSalidaId(Long salidaId) { this.salidaId = salidaId; }
	public Integer getNumAsientos() { return numAsientos; }
	public void setNumAsientos(Integer numAsientos) { this.numAsientos = numAsientos; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }

}
