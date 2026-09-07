package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalidaCreateRequest {

	private Long paqueteId;
	private LocalDate fechaSalida;
	private LocalDate fechaRegreso;
	private Integer cupoTotal;
	private BigDecimal precioPorPersona;
	private Long camionetaId;
	private String estado;

	public Long getPaqueteId() { return paqueteId; }
	public void setPaqueteId(Long paqueteId) { this.paqueteId = paqueteId; }
	public LocalDate getFechaSalida() { return fechaSalida; }
	public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
	public LocalDate getFechaRegreso() { return fechaRegreso; }
	public void setFechaRegreso(LocalDate fechaRegreso) { this.fechaRegreso = fechaRegreso; }
	public Integer getCupoTotal() { return cupoTotal; }
	public void setCupoTotal(Integer cupoTotal) { this.cupoTotal = cupoTotal; }
	public BigDecimal getPrecioPorPersona() { return precioPorPersona; }
	public void setPrecioPorPersona(BigDecimal precioPorPersona) { this.precioPorPersona = precioPorPersona; }
	public Long getCamionetaId() { return camionetaId; }
	public void setCamionetaId(Long camionetaId) { this.camionetaId = camionetaId; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }

}
