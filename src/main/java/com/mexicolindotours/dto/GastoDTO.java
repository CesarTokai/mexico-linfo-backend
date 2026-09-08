package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoDTO {

	private Long id;
	private Long viajeId;
	private String tipo;
	private String descripcion;
	private LocalDate fecha;
	private BigDecimal monto;
	private String notas;

	public GastoDTO() {
	}

	public GastoDTO(Long id, Long viajeId, String tipo, String descripcion, LocalDate fecha, BigDecimal monto, String notas) {
		this.id = id;
		this.viajeId = viajeId;
		this.tipo = tipo;
		this.descripcion = descripcion;
		this.fecha = fecha;
		this.monto = monto;
		this.notas = notas;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Long getViajeId() { return viajeId; }
	public void setViajeId(Long viajeId) { this.viajeId = viajeId; }
	public String getTipo() { return tipo; }
	public void setTipo(String tipo) { this.tipo = tipo; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public LocalDate getFecha() { return fecha; }
	public void setFecha(LocalDate fecha) { this.fecha = fecha; }
	public BigDecimal getMonto() { return monto; }
	public void setMonto(BigDecimal monto) { this.monto = monto; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }

}
