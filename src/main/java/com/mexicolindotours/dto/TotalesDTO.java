package com.mexicolindotours.dto;

import java.math.BigDecimal;

public class TotalesDTO {

	private Integer mes;
	private Integer anio;
	private BigDecimal ingresosTotal;
	private BigDecimal egresosViajes;
	private BigDecimal egresosCamionetas;
	private BigDecimal egresosGenerales;
	private BigDecimal egresosTotal;
	private BigDecimal neto;
	private BigDecimal pendientePorCobrar;

	public TotalesDTO() {
	}

	public TotalesDTO(Integer mes, Integer anio, BigDecimal ingresosTotal, BigDecimal egresosViajes,
					  BigDecimal egresosCamionetas, BigDecimal egresosGenerales, BigDecimal egresosTotal,
					  BigDecimal neto, BigDecimal pendientePorCobrar) {
		this.mes = mes;
		this.anio = anio;
		this.ingresosTotal = ingresosTotal;
		this.egresosViajes = egresosViajes;
		this.egresosCamionetas = egresosCamionetas;
		this.egresosGenerales = egresosGenerales;
		this.egresosTotal = egresosTotal;
		this.neto = neto;
		this.pendientePorCobrar = pendientePorCobrar;
	}

	public Integer getMes() { return mes; }
	public void setMes(Integer mes) { this.mes = mes; }
	public Integer getAnio() { return anio; }
	public void setAnio(Integer anio) { this.anio = anio; }
	public BigDecimal getIngresosTotal() { return ingresosTotal; }
	public void setIngresosTotal(BigDecimal ingresosTotal) { this.ingresosTotal = ingresosTotal; }
	public BigDecimal getEgresosViajes() { return egresosViajes; }
	public void setEgresosViajes(BigDecimal egresosViajes) { this.egresosViajes = egresosViajes; }
	public BigDecimal getEgresosCamionetas() { return egresosCamionetas; }
	public void setEgresosCamionetas(BigDecimal egresosCamionetas) { this.egresosCamionetas = egresosCamionetas; }
	public BigDecimal getEgresosGenerales() { return egresosGenerales; }
	public void setEgresosGenerales(BigDecimal egresosGenerales) { this.egresosGenerales = egresosGenerales; }
	public BigDecimal getEgresosTotal() { return egresosTotal; }
	public void setEgresosTotal(BigDecimal egresosTotal) { this.egresosTotal = egresosTotal; }
	public BigDecimal getNeto() { return neto; }
	public void setNeto(BigDecimal neto) { this.neto = neto; }
	public BigDecimal getPendientePorCobrar() { return pendientePorCobrar; }
	public void setPendientePorCobrar(BigDecimal pendientePorCobrar) { this.pendientePorCobrar = pendientePorCobrar; }

}
