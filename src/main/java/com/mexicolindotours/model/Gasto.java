package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gasto")
public class Gasto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "viaje_id", nullable = false)
	private Viaje viaje;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Tipo tipo;

	@Column(length = 160)
	private String descripcion;

	@Column(nullable = false)
	private LocalDate fecha;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal monto;

	@Column(length = 255)
	private String notas;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public enum Tipo {
		caseta, gasolina, chofer, otros
	}

	public Gasto() {
	}

	public Gasto(Viaje viaje, Tipo tipo, BigDecimal monto) {
		this.viaje = viaje;
		this.tipo = tipo;
		this.monto = monto;
		this.fecha = LocalDate.now();
	}

	public Gasto(Viaje viaje, Tipo tipo, LocalDate fecha, BigDecimal monto) {
		this.viaje = viaje;
		this.tipo = tipo;
		this.fecha = fecha;
		this.monto = monto;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Viaje getViaje() { return viaje; }
	public void setViaje(Viaje viaje) { this.viaje = viaje; }
	public Tipo getTipo() { return tipo; }
	public void setTipo(Tipo tipo) { this.tipo = tipo; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public LocalDate getFecha() { return fecha; }
	public void setFecha(LocalDate fecha) { this.fecha = fecha; }
	public BigDecimal getMonto() { return monto; }
	public void setMonto(BigDecimal monto) { this.monto = monto; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
