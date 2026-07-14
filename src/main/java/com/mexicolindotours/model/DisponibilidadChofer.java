package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilidad_chofer", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"chofer_id", "fecha"})
})
public class DisponibilidadChofer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "chofer_id", nullable = false)
	private Chofer chofer;

	@Column(nullable = false)
	private LocalDate fecha;

	@Column(nullable = false)
	private Boolean disponible = true;

	@Column(length = 160)
	private String notas;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public DisponibilidadChofer() {
	}

	public DisponibilidadChofer(Chofer chofer, LocalDate fecha, Boolean disponible) {
		this.chofer = chofer;
		this.fecha = fecha;
		this.disponible = disponible;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Chofer getChofer() { return chofer; }
	public void setChofer(Chofer chofer) { this.chofer = chofer; }
	public LocalDate getFecha() { return fecha; }
	public void setFecha(LocalDate fecha) { this.fecha = fecha; }
	public Boolean getDisponible() { return disponible; }
	public void setDisponible(Boolean disponible) { this.disponible = disponible; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
