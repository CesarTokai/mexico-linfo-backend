package com.mexicolindotours.dto;

import java.time.LocalDate;
import java.util.List;

public class PostDTO {

	private Long id;
	private String titulo;
	private String slug;
	private String resumen;
	private String contenido;
	private String imagenUrl;
	private String autor;
	private LocalDate fechaPublicacion;
	private String estado;
	private Long categoriaId;
	private String categoriaNombre;
	private String categoriaSlug;
	private List<EtiquetaDTO> etiquetas;

	public PostDTO(Long id, String titulo, String slug, String resumen, String contenido, String imagenUrl,
				   String autor, LocalDate fechaPublicacion, String estado,
				   Long categoriaId, String categoriaNombre, String categoriaSlug, List<EtiquetaDTO> etiquetas) {
		this.id = id;
		this.titulo = titulo;
		this.slug = slug;
		this.resumen = resumen;
		this.contenido = contenido;
		this.imagenUrl = imagenUrl;
		this.autor = autor;
		this.fechaPublicacion = fechaPublicacion;
		this.estado = estado;
		this.categoriaId = categoriaId;
		this.categoriaNombre = categoriaNombre;
		this.categoriaSlug = categoriaSlug;
		this.etiquetas = etiquetas;
	}

	public Long getId() { return id; }
	public String getTitulo() { return titulo; }
	public String getSlug() { return slug; }
	public String getResumen() { return resumen; }
	public String getContenido() { return contenido; }
	public String getImagenUrl() { return imagenUrl; }
	public String getAutor() { return autor; }
	public LocalDate getFechaPublicacion() { return fechaPublicacion; }
	public String getEstado() { return estado; }
	public Long getCategoriaId() { return categoriaId; }
	public String getCategoriaNombre() { return categoriaNombre; }
	public String getCategoriaSlug() { return categoriaSlug; }
	public List<EtiquetaDTO> getEtiquetas() { return etiquetas; }

}
