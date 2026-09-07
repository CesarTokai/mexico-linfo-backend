package com.mexicolindotours.dto;

import java.math.BigDecimal;

public class HistorialCamionetaDTO {

	private Long camionetaId;
	private String camionetaNombre;
	private Integer totalViajes;
	private Integer kmActual;
	private BigDecimal costosMantenimiento;
	private BigDecimal costosTramites;
	private Integer totalSalidasPublicas;
	private BigDecimal ingresosSalidasPublicas;

	public HistorialCamionetaDTO() {
	}

	public HistorialCamionetaDTO(Long camionetaId, String camionetaNombre, Integer totalViajes, Integer kmActual, BigDecimal costosMantenimiento, BigDecimal costosTramites, Integer totalSalidasPublicas, BigDecimal ingresosSalidasPublicas) {
		this.totalSalidasPublicas = totalSalidasPublicas;
		this.ingresosSalidasPublicas = ingresosSalidasPublicas;
		this.camionetaId = camionetaId;
		this.camionetaNombre = camionetaNombre;
		this.totalViajes = totalViajes;
		this.kmActual = kmActual;
		this.costosMantenimiento = costosMantenimiento;
		this.costosTramites = costosTramites;
	}

	public Long getCamionetaId() { return camionetaId; }
	public void setCamionetaId(Long camionetaId) { this.camionetaId = camionetaId; }
	public String getCamionetaNombre() { return camionetaNombre; }
	public void setCamionetaNombre(String camionetaNombre) { this.camionetaNombre = camionetaNombre; }
	public Integer getTotalViajes() { return totalViajes; }
	public void setTotalViajes(Integer totalViajes) { this.totalViajes = totalViajes; }
	public Integer getKmActual() { return kmActual; }
	public void setKmActual(Integer kmActual) { this.kmActual = kmActual; }
	public BigDecimal getCostosMantenimiento() { return costosMantenimiento; }
	public void setCostosMantenimiento(BigDecimal costosMantenimiento) { this.costosMantenimiento = costosMantenimiento; }
	public BigDecimal getCostosTramites() { return costosTramites; }
	public void setCostosTramites(BigDecimal costosTramites) { this.costosTramites = costosTramites; }

	public Integer getTotalSalidasPublicas() { return totalSalidasPublicas; }
	public void setTotalSalidasPublicas(Integer totalSalidasPublicas) { this.totalSalidasPublicas = totalSalidasPublicas; }
	public BigDecimal getIngresosSalidasPublicas() { return ingresosSalidasPublicas; }
	public void setIngresosSalidasPublicas(BigDecimal ingresosSalidasPublicas) { this.ingresosSalidasPublicas = ingresosSalidasPublicas; }

}
