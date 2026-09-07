package com.mexicolindotours.dto;

import java.time.LocalDate;
import java.util.List;

/** Lo que necesita el listado del blog: imagen, titulo, fecha y autor. */
public class PostResumenDTO {

	private Long id;
	private String titulo;
	private String slug;
	private String resumen;
	private String imagenUrl;
	private String autor;
	private LocalDate fechaPublicacion;
	private String categoriaNombre;
	private String categoriaSlug;
	private List<EtiquetaDTO> etiquetas;

	public PostResumenDTO(Long id, String titulo, String slug, String resumen, String imagenUrl,
						  String autor, LocalDate fechaPublicacion, String categoriaNombre, String categoriaSlug,
						  List<EtiquetaDTO> etiquetas) {
		this.id = id;
		this.titulo = titulo;
		this.slug = slug;
		this.resumen = resumen;
		this.imagenUrl = imagenUrl;
		this.autor = autor;
		this.fechaPublicacion = fechaPublicacion;
		this.categoriaNombre = categoriaNombre;
		this.categoriaSlug = categoriaSlug;
		this.etiquetas = etiquetas;
	}

	public Long getId() { return id; }
	public String getTitulo() { return titulo; }
	public String getSlug() { return slug; }
	public String getResumen() { return resumen; }
	public String getImagenUrl() { return imagenUrl; }
	public String getAutor() { return autor; }
	public LocalDate getFechaPublicacion() { return fechaPublicacion; }
	public String getCategoriaNombre() { return categoriaNombre; }
	public String getCategoriaSlug() { return categoriaSlug; }
	public List<EtiquetaDTO> getEtiquetas() { return etiquetas; }

}
