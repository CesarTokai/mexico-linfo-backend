package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gasto_general")
public class GastoGeneral {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate fecha;

	@Column(nullable = false, length = 200)
	private String descripcion;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal monto;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public GastoGeneral() {
	}

	public GastoGeneral(LocalDate fecha, String descripcion, BigDecimal monto) {
		this.fecha = fecha;
		this.descripcion = descripcion;
		this.monto = monto;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public LocalDate getFecha() { return fecha; }
	public void setFecha(LocalDate fecha) { this.fecha = fecha; }
	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	public BigDecimal getMonto() { return monto; }
	public void setMonto(BigDecimal monto) { this.monto = monto; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
