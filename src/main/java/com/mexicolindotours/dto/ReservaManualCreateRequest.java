package com.mexicolindotours.dto;

import java.math.BigDecimal;

/** Reserva que el personal registra a mano: WhatsApp, walk-in, o camioneta completa para un evento. */
public class ReservaManualCreateRequest {

	private Long salidaId;
	private Integer numAsientos;
	private String nombreCliente;
	private String telefonoCliente;
	private String correoCliente;
	private String notas;

	/** Si ya se cobro (aunque sea por WhatsApp/transferencia ya verificada). */
	private boolean marcarConfirmada;

	/** Monto verificado, si marcarConfirmada. Sin especificar, se asume el anticipo. */
	private BigDecimal montoRecibido;

	public Long getSalidaId() { return salidaId; }
	public void setSalidaId(Long salidaId) { this.salidaId = salidaId; }
	public Integer getNumAsientos() { return numAsientos; }
	public void setNumAsientos(Integer numAsientos) { this.numAsientos = numAsientos; }
	public String getNombreCliente() { return nombreCliente; }
	public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
	public String getTelefonoCliente() { return telefonoCliente; }
	public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }
	public String getCorreoCliente() { return correoCliente; }
	public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }
	public boolean isMarcarConfirmada() { return marcarConfirmada; }
	public void setMarcarConfirmada(boolean marcarConfirmada) { this.marcarConfirmada = marcarConfirmada; }
	public BigDecimal getMontoRecibido() { return montoRecibido; }
	public void setMontoRecibido(BigDecimal montoRecibido) { this.montoRecibido = montoRecibido; }

}
