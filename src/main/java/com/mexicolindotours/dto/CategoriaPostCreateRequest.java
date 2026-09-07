package com.mexicolindotours.dto;

public class CategoriaPostCreateRequest {

	private String nombre;
	private String slug;
	private String descripcion;

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getSlug() { return slug; }
	public void setSlug(String slug) { this.slug = slug; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

}
