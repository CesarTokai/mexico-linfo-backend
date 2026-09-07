package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "etiqueta")
public class Etiqueta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 60)
	private String nombre;

	@Column(nullable = false, unique = true, length = 80)
	private String slug;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public Etiqueta() {
	}

	public Etiqueta(String nombre, String slug) {
		this.nombre = nombre;
		this.slug = slug;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getSlug() { return slug; }
	public void setSlug(String slug) { this.slug = slug; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
