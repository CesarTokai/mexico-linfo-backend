package com.mexicolindotours.dto;

public class CategoriaPostDTO {

	private Long id;
	private String nombre;
	private String slug;
	private String descripcion;
	private Long totalPosts;

	public CategoriaPostDTO(Long id, String nombre, String slug, String descripcion, Long totalPosts) {
		this.id = id;
		this.nombre = nombre;
		this.slug = slug;
		this.descripcion = descripcion;
		this.totalPosts = totalPosts;
	}

	public Long getId() { return id; }
	public String getNombre() { return nombre; }
	public String getSlug() { return slug; }
	public String getDescripcion() { return descripcion; }
	public Long getTotalPosts() { return totalPosts; }

}
