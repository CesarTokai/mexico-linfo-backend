package com.mexicolindotours.dto;

import java.math.BigDecimal;

public class HistorialChoferDTO {

	private Long choferId;
	private String choferNombre;
	private Integer totalViajes;
	private Integer kmManejados;
	private BigDecimal totalPagado;

	public HistorialChoferDTO() {
	}

	public HistorialChoferDTO(Long choferId, String choferNombre, Integer totalViajes, Integer kmManejados, BigDecimal totalPagado) {
		this.choferId = choferId;
		this.choferNombre = choferNombre;
		this.totalViajes = totalViajes;
		this.kmManejados = kmManejados;
		this.totalPagado = totalPagado;
	}

	public Long getChoferId() { return choferId; }
	public void setChoferId(Long choferId) { this.choferId = choferId; }
	public String getChoferNombre() { return choferNombre; }
	public void setChoferNombre(String choferNombre) { this.choferNombre = choferNombre; }
	public Integer getTotalViajes() { return totalViajes; }
	public void setTotalViajes(Integer totalViajes) { this.totalViajes = totalViajes; }
	public Integer getKmManejados() { return kmManejados; }
	public void setKmManejados(Integer kmManejados) { this.kmManejados = kmManejados; }
	public BigDecimal getTotalPagado() { return totalPagado; }
	public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }

}
