package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categoria_post")
public class CategoriaPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 80)
	private String nombre;

	@Column(nullable = false, unique = true, length = 100)
	private String slug;

	@Column(length = 255)
	private String descripcion;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public CategoriaPost() {
	}

	public CategoriaPost(String nombre, String slug, String descripcion) {
		this.nombre = nombre;
		this.slug = slug;
		this.descripcion = descripcion;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getSlug() { return slug; }
	public void setSlug(String slug) { this.slug = slug; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
