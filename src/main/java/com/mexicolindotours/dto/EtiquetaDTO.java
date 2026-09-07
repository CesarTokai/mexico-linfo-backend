package com.mexicolindotours.dto;

public class EtiquetaDTO {

	private Long id;
	private String nombre;
	private String slug;
	private Long totalPosts;

	public EtiquetaDTO(Long id, String nombre, String slug, Long totalPosts) {
		this.id = id;
		this.nombre = nombre;
		this.slug = slug;
		this.totalPosts = totalPosts;
	}

	public Long getId() { return id; }
	public String getNombre() { return nombre; }
	public String getSlug() { return slug; }
	public Long getTotalPosts() { return totalPosts; }

}
