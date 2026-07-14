package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ViajeDTO {

	private Long id;
	private Long clienteId;
	private String clienteNombre;
	private Long camionetaId;
	private String camionetaNombre;
	private Long choferId;
	private String choferNombre;
	private String concepto;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private Integer kmInicial;
	private Integer kmFinal;
	private BigDecimal costoTotal;
	private String estado;
	private String notas;
	private List<PagoDTO> pagos;
	private List<GastoDTO> gastos;

	public ViajeDTO() {
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Long getClienteId() { return clienteId; }
	public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
	public String getClienteNombre() { return clienteNombre; }
	public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
	public Long getCamionetaId() { return camionetaId; }
	public void setCamionetaId(Long camionetaId) { this.camionetaId = camionetaId; }
	public String getCamionetaNombre() { return camionetaNombre; }
	public void setCamionetaNombre(String camionetaNombre) { this.camionetaNombre = camionetaNombre; }
	public Long getChoferId() { return choferId; }
	public void setChoferId(Long choferId) { this.choferId = choferId; }
	public String getChoferNombre() { return choferNombre; }
	public void setChoferNombre(String choferNombre) { this.choferNombre = choferNombre; }
	public String getConcepto() { return concepto; }
	public void setConcepto(String concepto) { this.concepto = concepto; }
	public LocalDate getFechaInicio() { return fechaInicio; }
	public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
	public LocalDate getFechaFin() { return fechaFin; }
	public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
	public Integer getKmInicial() { return kmInicial; }
	public void setKmInicial(Integer kmInicial) { this.kmInicial = kmInicial; }
	public Integer getKmFinal() { return kmFinal; }
	public void setKmFinal(Integer kmFinal) { this.kmFinal = kmFinal; }
	public BigDecimal getCostoTotal() { return costoTotal; }
	public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }
	public List<PagoDTO> getPagos() { return pagos; }
	public void setPagos(List<PagoDTO> pagos) { this.pagos = pagos; }
	public List<GastoDTO> getGastos() { return gastos; }
	public void setGastos(List<GastoDTO> gastos) { this.gastos = gastos; }

}
