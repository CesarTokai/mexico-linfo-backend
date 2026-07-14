package com.mexicolindotours.dto;

import java.math.BigDecimal;

public class GastoDTO {

	private Long id;
	private Long viajeId;
	private String tipo;
	private String descripcion;
	private BigDecimal monto;

	public GastoDTO() {
	}

	public GastoDTO(Long id, Long viajeId, String tipo, String descripcion, BigDecimal monto) {
		this.id = id;
		this.viajeId = viajeId;
		this.tipo = tipo;
		this.descripcion = descripcion;
		this.monto = monto;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Long getViajeId() { return viajeId; }
	public void setViajeId(Long viajeId) { this.viajeId = viajeId; }
	public String getTipo() { return tipo; }
	public void setTipo(String tipo) { this.tipo = tipo; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public BigDecimal getMonto() { return monto; }
	public void setMonto(BigDecimal monto) { this.monto = monto; }

}
