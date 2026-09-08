package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalidaDTO {

	private Long id;
	private Long paqueteId;
	private String paqueteTitulo;
	private String paqueteSlug;
	private String destino;
	private String imagenUrl;
	private LocalDate fechaSalida;
	private LocalDate fechaRegreso;
	private Integer cupoTotal;
	private Integer asientosDisponibles;
	private BigDecimal precioPorPersona;
	private String estado;
	private Long camionetaId;
	private String camionetaNombre;
	private Long choferId;
	private String choferNombre;

	public SalidaDTO(Long id, Long paqueteId, String paqueteTitulo, String paqueteSlug, String destino, String imagenUrl,
					 LocalDate fechaSalida, LocalDate fechaRegreso, Integer cupoTotal, Integer asientosDisponibles,
					 BigDecimal precioPorPersona, String estado, Long camionetaId, String camionetaNombre,
					 Long choferId, String choferNombre) {
		this.id = id;
		this.paqueteId = paqueteId;
		this.paqueteTitulo = paqueteTitulo;
		this.paqueteSlug = paqueteSlug;
		this.destino = destino;
		this.imagenUrl = imagenUrl;
		this.fechaSalida = fechaSalida;
		this.fechaRegreso = fechaRegreso;
		this.cupoTotal = cupoTotal;
		this.asientosDisponibles = asientosDisponibles;
		this.precioPorPersona = precioPorPersona;
		this.estado = estado;
		this.camionetaId = camionetaId;
		this.camionetaNombre = camionetaNombre;
		this.choferId = choferId;
		this.choferNombre = choferNombre;
	}

	public Long getId() { return id; }
	public Long getPaqueteId() { return paqueteId; }
	public String getPaqueteTitulo() { return paqueteTitulo; }
	public String getPaqueteSlug() { return paqueteSlug; }
	public String getDestino() { return destino; }
	public String getImagenUrl() { return imagenUrl; }
	public LocalDate getFechaSalida() { return fechaSalida; }
	public LocalDate getFechaRegreso() { return fechaRegreso; }
	public Integer getCupoTotal() { return cupoTotal; }
	public Integer getAsientosDisponibles() { return asientosDisponibles; }
	public BigDecimal getPrecioPorPersona() { return precioPorPersona; }
	public String getEstado() { return estado; }
	public Long getCamionetaId() { return camionetaId; }
	public String getCamionetaNombre() { return camionetaNombre; }
	public Long getChoferId() { return choferId; }
	public String getChoferNombre() { return choferNombre; }

}
