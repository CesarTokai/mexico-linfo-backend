package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.util.List;

public class PaqueteDetalleDTO {

	private Long id;
	private String titulo;
	private String slug;
	private String resumen;
	private String descripcion;
	private String destino;
	private String categoria;
	private String imagenUrl;
	private BigDecimal precioPorPersona;
	private Integer duracionDias;
	private Boolean esFavorito;
	private List<SalidaDTO> salidas;

	public PaqueteDetalleDTO(Long id, String titulo, String slug, String resumen, String descripcion, String destino,
							 String categoria, String imagenUrl, BigDecimal precioPorPersona, Integer duracionDias,
							 Boolean esFavorito, List<SalidaDTO> salidas) {
		this.id = id;
		this.titulo = titulo;
		this.slug = slug;
		this.resumen = resumen;
		this.descripcion = descripcion;
		this.destino = destino;
		this.categoria = categoria;
		this.imagenUrl = imagenUrl;
		this.precioPorPersona = precioPorPersona;
		this.duracionDias = duracionDias;
		this.esFavorito = esFavorito;
		this.salidas = salidas;
	}

	public Long getId() { return id; }
	public String getTitulo() { return titulo; }
	public String getSlug() { return slug; }
	public String getResumen() { return resumen; }
	public String getDescripcion() { return descripcion; }
	public String getDestino() { return destino; }
	public String getCategoria() { return categoria; }
	public String getImagenUrl() { return imagenUrl; }
	public BigDecimal getPrecioPorPersona() { return precioPorPersona; }
	public Integer getDuracionDias() { return duracionDias; }
	public Boolean getEsFavorito() { return esFavorito; }
	public List<SalidaDTO> getSalidas() { return salidas; }

}
