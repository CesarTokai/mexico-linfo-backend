package com.mexicolindotours.dto;

import java.math.BigDecimal;

public class ConfirmarPagoRequest {

	/** Dinero verificado. Si viene vacio se asume el anticipo pendiente. */
	private BigDecimal montoRecibido;

	public BigDecimal getMontoRecibido() { return montoRecibido; }
	public void setMontoRecibido(BigDecimal montoRecibido) { this.montoRecibido = montoRecibido; }

}
