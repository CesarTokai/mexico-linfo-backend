package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tramite_vehiculo")
public class TramiteVehiculo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "camioneta_id", nullable = false)
	private Camioneta camioneta;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Tipo tipo;

	@Column(nullable = false)
	private LocalDate fechaPago;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal monto;

	@Column(name = "fecha_vencimiento")
	private LocalDate fechaVencimiento;

	@Column(length = 255)
	private String notas;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public enum Tipo {
		tenencia, placas, seguro, verificacion, otro
	}

	public TramiteVehiculo() {
	}

	public TramiteVehiculo(Camioneta camioneta, Tipo tipo, LocalDate fechaPago, BigDecimal monto) {
		this.camioneta = camioneta;
		this.tipo = tipo;
		this.fechaPago = fechaPago;
		this.monto = monto;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Camioneta getCamioneta() { return camioneta; }
	public void setCamioneta(Camioneta camioneta) { this.camioneta = camioneta; }
	public Tipo getTipo() { return tipo; }
	public void setTipo(Tipo tipo) { this.tipo = tipo; }
	public LocalDate getFechaPago() { return fechaPago; }
	public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
	public BigDecimal getMonto() { return monto; }
	public void setMonto(BigDecimal monto) { this.monto = monto; }
	public LocalDate getFechaVencimiento() { return fechaVencimiento; }
	public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
