package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "camioneta")
public class Camioneta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 60)
	private String nombre;

	@Column(nullable = false, length = 60)
	private String modelo;

	@Column(nullable = false)
	private Integer capacidad;

	@Column(nullable = false)
	private Integer kmActual;

	@Column(nullable = false)
	private Integer intervaloMantenimientoKm;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Estado estado = Estado.activa;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	public enum Estado {
		activa, en_taller, baja
	}

	public Camioneta() {
	}

	public Camioneta(String nombre, String modelo, Integer capacidad) {
		this.nombre = nombre;
		this.modelo = modelo;
		this.capacidad = capacidad;
		this.kmActual = 0;
		this.intervaloMantenimientoKm = 10000;
		this.estado = Estado.activa;
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
	public Estado getEstado() { return estado; }
	public void setEstado(Estado estado) { this.estado = estado; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
