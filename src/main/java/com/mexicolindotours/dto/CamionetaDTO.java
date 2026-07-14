package com.mexicolindotours.dto;

public class CamionetaDTO {

	private Long id;
	private String nombre;
	private String modelo;
	private Integer capacidad;
	private Integer kmActual;
	private Integer intervaloMantenimientoKm;
	private String estado;

	public CamionetaDTO() {
	}

	public CamionetaDTO(Long id, String nombre, String modelo, Integer capacidad, Integer kmActual, Integer intervaloMantenimientoKm, String estado) {
		this.id = id;
		this.nombre = nombre;
		this.modelo = modelo;
		this.capacidad = capacidad;
		this.kmActual = kmActual;
		this.intervaloMantenimientoKm = intervaloMantenimientoKm;
		this.estado = estado;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getModelo() { return modelo; }
	public void setModelo(String modelo) { this.modelo = modelo; }
	public Integer getCapacidad() { return capacidad; }
	public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
	public Integer getKmActual() { return kmActual; }
	public void setKmActual(Integer kmActual) { this.kmActual = kmActual; }
	public Integer getIntervaloMantenimientoKm() { return intervaloMantenimientoKm; }
	public void setIntervaloMantenimientoKm(Integer intervaloMantenimientoKm) { this.intervaloMantenimientoKm = intervaloMantenimientoKm; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }

}
