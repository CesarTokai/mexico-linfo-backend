package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Fecha concreta en que sale un paquete, con su cupo de asientos. */
@Entity
@Table(name = "salida")
public class Salida {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "paquete_id")
	private Paquete paquete;

	@Column(name = "fecha_salida", nullable = false)
	private LocalDate fechaSalida;

	@Column(name = "fecha_regreso", nullable = false)
	private LocalDate fechaRegreso;

	@Column(name = "cupo_total", nullable = false)
	private Integer cupoTotal;

	/** Si es null se usa el precio del paquete. */
	@Column(name = "precio_por_persona", precision = 10, scale = 2)
	private BigDecimal precioPorPersona;

	/** Unidad asignada. Puede quedar null hasta que se confirme la salida. */
	@ManyToOne
	@JoinColumn(name = "camioneta_id")
	private Camioneta camioneta;

	/** Chofer asignado. Igual que la unidad, puede completarse despues. */
	@ManyToOne
	@JoinColumn(name = "chofer_id")
	private Chofer chofer;

	/** Viaje interno generado cuando la salida se opera. */
	@ManyToOne
	@JoinColumn(name = "viaje_id")
	private Viaje viaje;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Estado estado = Estado.programada;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	public enum Estado {
		programada, cerrada, cancelada
	}

	public Salida() {
	}

	public Salida(Paquete paquete, LocalDate fechaSalida, LocalDate fechaRegreso, Integer cupoTotal) {
		this.paquete = paquete;
		this.fechaSalida = fechaSalida;
		this.fechaRegreso = fechaRegreso;
		this.cupoTotal = cupoTotal;
		this.estado = Estado.programada;
	}

	/** Precio efectivo: el de la salida si lo tiene, si no el del paquete. */
	public BigDecimal precioEfectivo() {
		return precioPorPersona != null ? precioPorPersona : paquete.getPrecioPorPersona();
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Paquete getPaquete() { return paquete; }
	public void setPaquete(Paquete paquete) { this.paquete = paquete; }
	public LocalDate getFechaSalida() { return fechaSalida; }
	public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
	public LocalDate getFechaRegreso() { return fechaRegreso; }
	public void setFechaRegreso(LocalDate fechaRegreso) { this.fechaRegreso = fechaRegreso; }
	public Integer getCupoTotal() { return cupoTotal; }
	public void setCupoTotal(Integer cupoTotal) { this.cupoTotal = cupoTotal; }
	public BigDecimal getPrecioPorPersona() { return precioPorPersona; }
	public void setPrecioPorPersona(BigDecimal precioPorPersona) { this.precioPorPersona = precioPorPersona; }
	public Camioneta getCamioneta() { return camioneta; }
	public void setCamioneta(Camioneta camioneta) { this.camioneta = camioneta; }
	public Chofer getChofer() { return chofer; }
	public void setChofer(Chofer chofer) { this.chofer = chofer; }
	public Viaje getViaje() { return viaje; }
	public void setViaje(Viaje viaje) { this.viaje = viaje; }
	public Estado getEstado() { return estado; }
	public void setEstado(Estado estado) { this.estado = estado; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
