package com.mexicolindotours.dto;

import java.math.BigDecimal;

public class PaqueteCreateRequest {

	private String titulo;
	private String slug;
	private String resumen;
	private String descripcion;
	private String destino;
	private String categoria;
	private String imagenUrl;
	private BigDecimal precioPorPersona;
	private Integer duracionDias;
	private Boolean activo;

	public String getTitulo() { return titulo; }
	public void setTitulo(String titulo) { this.titulo = titulo; }
	public String getSlug() { return slug; }
	public void setSlug(String slug) { this.slug = slug; }
	public String getResumen() { return resumen; }
	public void setResumen(String resumen) { this.resumen = resumen; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public String getDestino() { return destino; }
	public void setDestino(String destino) { this.destino = destino; }
	public String getCategoria() { return categoria; }
	public void setCategoria(String categoria) { this.categoria = categoria; }
	public String getImagenUrl() { return imagenUrl; }
	public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
	public BigDecimal getPrecioPorPersona() { return precioPorPersona; }
	public void setPrecioPorPersona(BigDecimal precioPorPersona) { this.precioPorPersona = precioPorPersona; }
	public Integer getDuracionDias() { return duracionDias; }
	public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }
	public Boolean getActivo() { return activo; }
	public void setActivo(Boolean activo) { this.activo = activo; }

}
