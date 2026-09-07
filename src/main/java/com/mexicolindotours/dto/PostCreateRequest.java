package com.mexicolindotours.dto;

import java.time.LocalDate;
import java.util.List;

public class PostCreateRequest {

	private String titulo;
	private String slug;
	private String resumen;
	private String contenido;
	private String imagenUrl;
	private String autor;
	private Long categoriaId;
	private List<Long> etiquetaIds;
	private String estado;
	private LocalDate fechaPublicacion;

	public String getTitulo() { return titulo; }
	public void setTitulo(String titulo) { this.titulo = titulo; }
	public String getSlug() { return slug; }
	public void setSlug(String slug) { this.slug = slug; }
	public String getResumen() { return resumen; }
	public void setResumen(String resumen) { this.resumen = resumen; }
	public String getContenido() { return contenido; }
	public void setContenido(String contenido) { this.contenido = contenido; }
	public String getImagenUrl() { return imagenUrl; }
	public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
	public String getAutor() { return autor; }
	public void setAutor(String autor) { this.autor = autor; }
	public Long getCategoriaId() { return categoriaId; }
	public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
	public List<Long> getEtiquetaIds() { return etiquetaIds; }
	public void setEtiquetaIds(List<Long> etiquetaIds) { this.etiquetaIds = etiquetaIds; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }
	public LocalDate getFechaPublicacion() { return fechaPublicacion; }
	public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

}
