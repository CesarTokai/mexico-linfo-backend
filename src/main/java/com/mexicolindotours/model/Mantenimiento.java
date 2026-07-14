package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mantenimiento")
public class Mantenimiento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "camioneta_id", nullable = false)
	private Camioneta camioneta;

	@Column(nullable = false)
	private LocalDate fecha;

	@Column(name = "km_al_momento")
	private Integer kmAlMomento;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Tipo tipo;

	@Column(length = 200)
	private String descripcion;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal costo;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public enum Tipo {
		mantenimiento, refaccion
	}

	public Mantenimiento() {
	}

	public Mantenimiento(Camioneta camioneta, LocalDate fecha, Tipo tipo, BigDecimal costo) {
		this.camioneta = camioneta;
		this.fecha = fecha;
		this.tipo = tipo;
		this.costo = costo;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Camioneta getCamioneta() { return camioneta; }
	public void setCamioneta(Camioneta camioneta) { this.camioneta = camioneta; }
	public LocalDate getFecha() { return fecha; }
	public void setFecha(LocalDate fecha) { this.fecha = fecha; }
	public Integer getKmAlMomento() { return kmAlMomento; }
	public void setKmAlMomento(Integer kmAlMomento) { this.kmAlMomento = kmAlMomento; }
	public Tipo getTipo() { return tipo; }
	public void setTipo(Tipo tipo) { this.tipo = tipo; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public BigDecimal getCosto() { return costo; }
	public void setCosto(BigDecimal costo) { this.costo = costo; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
