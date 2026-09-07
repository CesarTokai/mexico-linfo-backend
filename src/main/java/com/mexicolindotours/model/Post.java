package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "post")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String titulo;

	@Column(nullable = false, unique = true, length = 220)
	private String slug;

	@Column(length = 500)
	private String resumen;

	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String contenido;

	@Column(name = "imagen_url", length = 500)
	private String imagenUrl;

	@Column(nullable = false, length = 120)
	private String autor;

	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private CategoriaPost categoria;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "post_etiqueta",
			joinColumns = @JoinColumn(name = "post_id"),
			inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
	)
	private Set<Etiqueta> etiquetas = new LinkedHashSet<>();

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Estado estado = Estado.borrador;

	@Column(name = "fecha_publicacion")
	private LocalDate fechaPublicacion;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	public enum Estado {
		borrador, publicado
	}

	public Post() {
	}

	public Post(String titulo, String slug, String contenido, String autor) {
		this.titulo = titulo;
		this.slug = slug;
		this.contenido = contenido;
		this.autor = autor;
		this.estado = Estado.borrador;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
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
	public CategoriaPost getCategoria() { return categoria; }
	public void setCategoria(CategoriaPost categoria) { this.categoria = categoria; }
	public Set<Etiqueta> getEtiquetas() { return etiquetas; }
	public void setEtiquetas(Set<Etiqueta> etiquetas) { this.etiquetas = etiquetas; }
	public Estado getEstado() { return estado; }
	public void setEstado(Estado estado) { this.estado = estado; }
	public LocalDate getFechaPublicacion() { return fechaPublicacion; }
	public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
