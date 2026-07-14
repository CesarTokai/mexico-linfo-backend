package com.mexicolindotours.dto;

import java.math.BigDecimal;

public class HistorialClienteDTO {

	private Long clienteId;
	private String clienteNombre;
	private Integer totalViajes;
	private BigDecimal totalPagado;
	private BigDecimal pendiente;

	public HistorialClienteDTO() {
	}

	public HistorialClienteDTO(Long clienteId, String clienteNombre, Integer totalViajes, BigDecimal totalPagado, BigDecimal pendiente) {
		this.clienteId = clienteId;
		this.clienteNombre = clienteNombre;
		this.totalViajes = totalViajes;
		this.totalPagado = totalPagado;
		this.pendiente = pendiente;
	}

	public Long getClienteId() { return clienteId; }
	public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
	public String getClienteNombre() { return clienteNombre; }
	public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
	public Integer getTotalViajes() { return totalViajes; }
	public void setTotalViajes(Integer totalViajes) { this.totalViajes = totalViajes; }
	public BigDecimal getTotalPagado() { return totalPagado; }
	public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }
	public BigDecimal getPendiente() { return pendiente; }
	public void setPendiente(BigDecimal pendiente) { this.pendiente = pendiente; }

}
