package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Apartado de asientos en una salida. El pago es por transferencia:
 * el cliente sube su comprobante y el personal lo verifica y confirma.
 */
@Entity
@Table(name = "reserva")
public class Reserva {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "salida_id")
	private Salida salida;

	@ManyToOne(optional = false)
	@JoinColumn(name = "usuario_publico_id")
	private UsuarioPublico usuarioPublico;

	@Column(name = "num_asientos", nullable = false)
	private Integer numAsientos;

	@Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal montoTotal;

	/** Lo minimo que hay que cubrir para asegurar los asientos (50% por defecto). */
	@Column(name = "monto_anticipo", nullable = false, precision = 10, scale = 2)
	private BigDecimal montoAnticipo = BigDecimal.ZERO;

	/** Dinero efectivamente verificado por el personal. */
	@Column(name = "monto_pagado", nullable = false, precision = 10, scale = 2)
	private BigDecimal montoPagado = BigDecimal.ZERO;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Estado estado = Estado.pendiente_pago;

	@Column(name = "comprobante_url", length = 500)
	private String comprobanteUrl;

	@Column(name = "referencia_transferencia", length = 120)
	private String referenciaTransferencia;

	@Column(length = 500)
	private String notas;

	@Column(name = "confirmada_at")
	private LocalDateTime confirmadaAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	/**
	 * pendiente_pago -> el cliente aparto pero aun no sube comprobante
	 * en_revision   -> subio comprobante, falta que el personal lo valide
	 * confirmada    -> pago verificado, asientos asegurados
	 * cancelada     -> libera los asientos
	 */
	public enum Estado {
		pendiente_pago, en_revision, confirmada, cancelada
	}

	public Reserva() {
	}

	public Reserva(Salida salida, UsuarioPublico usuarioPublico, Integer numAsientos,
				   BigDecimal montoTotal, BigDecimal montoAnticipo) {
		this.salida = salida;
		this.usuarioPublico = usuarioPublico;
		this.numAsientos = numAsientos;
		this.montoTotal = montoTotal;
		this.montoAnticipo = montoAnticipo;
		this.montoPagado = BigDecimal.ZERO;
		this.estado = Estado.pendiente_pago;
	}

	/** Lo que falta por cobrar. Nunca negativo. */
	public BigDecimal saldoPendiente() {
		BigDecimal saldo = montoTotal.subtract(montoPagado);
		return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
	}

	/** El anticipo ya esta cubierto: los asientos estan asegurados. */
	public boolean anticipoCubierto() {
		return montoPagado.compareTo(montoAnticipo) >= 0;
	}

	public boolean liquidada() {
		return montoPagado.compareTo(montoTotal) >= 0;
	}

	/** Una reserva ocupa cupo mientras no este cancelada. */
	public boolean ocupaCupo() {
		return estado != Estado.cancelada;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Salida getSalida() { return salida; }
	public void setSalida(Salida salida) { this.salida = salida; }
	public UsuarioPublico getUsuarioPublico() { return usuarioPublico; }
	public void setUsuarioPublico(UsuarioPublico usuarioPublico) { this.usuarioPublico = usuarioPublico; }
	public Integer getNumAsientos() { return numAsientos; }
	public void setNumAsientos(Integer numAsientos) { this.numAsientos = numAsientos; }
	public BigDecimal getMontoTotal() { return montoTotal; }
	public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
	public BigDecimal getMontoAnticipo() { return montoAnticipo; }
	public void setMontoAnticipo(BigDecimal montoAnticipo) { this.montoAnticipo = montoAnticipo; }
	public BigDecimal getMontoPagado() { return montoPagado; }
	public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
	public Estado getEstado() { return estado; }
	public void setEstado(Estado estado) { this.estado = estado; }
	public String getComprobanteUrl() { return comprobanteUrl; }
	public void setComprobanteUrl(String comprobanteUrl) { this.comprobanteUrl = comprobanteUrl; }
	public String getReferenciaTransferencia() { return referenciaTransferencia; }
	public void setReferenciaTransferencia(String referenciaTransferencia) { this.referenciaTransferencia = referenciaTransferencia; }
	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }
	public LocalDateTime getConfirmadaAt() { return confirmadaAt; }
	public void setConfirmadaAt(LocalDateTime confirmadaAt) { this.confirmadaAt = confirmadaAt; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
