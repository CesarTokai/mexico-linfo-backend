package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Producto que se muestra al publico: un destino con su descripcion y precio
 * de referencia. NO es un `viaje`: el viaje es el registro interno de la
 * operacion y lleva costos y datos que nunca salen al sitio.
 */
@Entity
@Table(name = "paquete")
public class Paquete {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String titulo;

	@Column(nullable = false, unique = true, length = 220)
	private String slug;

	@Column(length = 500)
	private String resumen;

	@Column(columnDefinition = "LONGTEXT")
	private String descripcion;

	/** Lo que se filtra en la busqueda por ubicacion. */
	@Column(nullable = false, length = 120)
	private String destino;

	@Column(length = 120)
	private String categoria;

	@Column(name = "imagen_url", length = 500)
	private String imagenUrl;

	@Column(name = "precio_por_persona", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioPorPersona;

	@Column(name = "duracion_dias")
	private Integer duracionDias;

	@Column(nullable = false)
	private Boolean activo = true;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	public Paquete() {
	}

	public Paquete(String titulo, String slug, String destino, BigDecimal precioPorPersona) {
		this.titulo = titulo;
		this.slug = slug;
		this.destino = destino;
		this.precioPorPersona = precioPorPersona;
		this.activo = true;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
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
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
