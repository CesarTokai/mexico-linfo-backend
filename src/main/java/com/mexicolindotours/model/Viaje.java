package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "viaje")
public class Viaje {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "camioneta_id", nullable = false)
	private Camioneta camioneta;

	@ManyToOne
	@JoinColumn(name = "chofer_id")
	private Chofer chofer;

	@Column(nullable = false, length = 200)
	private String concepto;

	@Column(nullable = false)
	private LocalDate fechaInicio;

	@Column(nullable = false)
	private LocalDate fechaFin;

	@Column(name = "km_inicial")
	private Integer kmInicial;

	@Column(name = "km_final")
	private Integer kmFinal;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal costoTotal;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Estado estado = Estado.apartado;

	@Column(length = 255)
	private String notas;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	public enum Estado {
		apartado, en_curso, finalizado, cancelado
	}

	public Viaje() {
	}

	public Viaje(Cliente cliente, Camioneta camioneta, String concepto, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal costoTotal) {
		this.cliente = cliente;
		this.camioneta = camioneta;
		this.concepto = concepto;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.costoTotal = costoTotal;
		this.estado = Estado.apartado;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Cliente getCliente() { return cliente; }
	public void setCliente(Cliente cliente) { this.cliente = cliente; }
	public Camioneta getCamioneta() { return camioneta; }
	public void setCamioneta(Camioneta camioneta) { this.camioneta = camioneta; }
	public Chofer getChofer() { return chofer; }
	public void setChofer(Chofer chofer) { this.chofer = chofer; }
	public String getConcepto() { return concepto; }
	public void setConcepto(String concepto) { this.concepto = concepto; }
	public LocalDate getFechaInicio() { return fechaInicio; }
	public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
	public LocalDate getFechaFin() { return fechaFin; }
	public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
	public Integer getKmInicial() { return kmInicial; }
	public void setKmInicial(Integer kmInicial) { this.kmInicial = kmInicial; }
	public Integer getKmFinal() { return kmFinal; }
	public void setKmFinal(Integer kmFinal) { this.kmFinal = kmFinal; }
	public BigDecimal getCostoTotal() { return costoTotal; }
	public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }
	public Estado getEstado() { return estado; }
	public void setEstado(Estado estado) { this.estado = estado; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
