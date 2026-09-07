package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaDTO {

	private Long id;
	private Long salidaId;
	private String paqueteTitulo;
	private String destino;
	private LocalDate fechaSalida;
	private LocalDate fechaRegreso;
	private Long usuarioPublicoId;
	private String clienteNombre;
	private String clienteCorreo;
	private String clienteTelefono;
	private Integer numAsientos;
	private BigDecimal montoTotal;
	private String estado;
	private String comprobanteUrl;
	private String referenciaTransferencia;
	private String notas;
	private LocalDateTime confirmadaAt;

	public ReservaDTO(Long id, Long salidaId, String paqueteTitulo, String destino, LocalDate fechaSalida,
					  LocalDate fechaRegreso, Long usuarioPublicoId, String clienteNombre, String clienteCorreo,
					  String clienteTelefono, Integer numAsientos, BigDecimal montoTotal, String estado,
					  String comprobanteUrl, String referenciaTransferencia, String notas, LocalDateTime confirmadaAt) {
		this.id = id;
		this.salidaId = salidaId;
		this.paqueteTitulo = paqueteTitulo;
		this.destino = destino;
		this.fechaSalida = fechaSalida;
		this.fechaRegreso = fechaRegreso;
		this.usuarioPublicoId = usuarioPublicoId;
		this.clienteNombre = clienteNombre;
		this.clienteCorreo = clienteCorreo;
		this.clienteTelefono = clienteTelefono;
		this.numAsientos = numAsientos;
		this.montoTotal = montoTotal;
		this.estado = estado;
		this.comprobanteUrl = comprobanteUrl;
		this.referenciaTransferencia = referenciaTransferencia;
		this.notas = notas;
		this.confirmadaAt = confirmadaAt;
	}

	public Long getId() { return id; }
	public Long getSalidaId() { return salidaId; }
	public String getPaqueteTitulo() { return paqueteTitulo; }
	public String getDestino() { return destino; }
	public LocalDate getFechaSalida() { return fechaSalida; }
	public LocalDate getFechaRegreso() { return fechaRegreso; }
	public Long getUsuarioPublicoId() { return usuarioPublicoId; }
	public String getClienteNombre() { return clienteNombre; }
	public String getClienteCorreo() { return clienteCorreo; }
	public String getClienteTelefono() { return clienteTelefono; }
	public Integer getNumAsientos() { return numAsientos; }
	public BigDecimal getMontoTotal() { return montoTotal; }
	public String getEstado() { return estado; }
	public String getComprobanteUrl() { return comprobanteUrl; }
	public String getReferenciaTransferencia() { return referenciaTransferencia; }
	public String getNotas() { return notas; }
	public LocalDateTime getConfirmadaAt() { return confirmadaAt; }

}
