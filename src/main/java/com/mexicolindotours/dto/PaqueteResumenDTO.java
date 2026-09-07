package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Tarjeta del catalogo publico. */
public class PaqueteResumenDTO {

	private Long id;
	private String titulo;
	private String slug;
	private String resumen;
	private String destino;
	private String categoria;
	private String imagenUrl;
	private BigDecimal precioPorPersona;
	private Integer duracionDias;
	private LocalDate proximaSalida;
	private Integer salidasDisponibles;
	private Boolean esFavorito;

	public PaqueteResumenDTO(Long id, String titulo, String slug, String resumen, String destino, String categoria,
							 String imagenUrl, BigDecimal precioPorPersona, Integer duracionDias,
							 LocalDate proximaSalida, Integer salidasDisponibles, Boolean esFavorito) {
		this.id = id;
		this.titulo = titulo;
		this.slug = slug;
		this.resumen = resumen;
		this.destino = destino;
		this.categoria = categoria;
		this.imagenUrl = imagenUrl;
		this.precioPorPersona = precioPorPersona;
		this.duracionDias = duracionDias;
		this.proximaSalida = proximaSalida;
		this.salidasDisponibles = salidasDisponibles;
		this.esFavorito = esFavorito;
	}

	public Long getId() { return id; }
	public String getTitulo() { return titulo; }
	public String getSlug() { return slug; }
	public String getResumen() { return resumen; }
	public String getDestino() { return destino; }
	public String getCategoria() { return categoria; }
	public String getImagenUrl() { return imagenUrl; }
	public BigDecimal getPrecioPorPersona() { return precioPorPersona; }
	public Integer getDuracionDias() { return duracionDias; }
	public LocalDate getProximaSalida() { return proximaSalida; }
	public Integer getSalidasDisponibles() { return salidasDisponibles; }
	public Boolean getEsFavorito() { return esFavorito; }

}
