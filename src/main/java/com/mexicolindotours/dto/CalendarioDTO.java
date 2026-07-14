package com.mexicolindotours.dto;

import java.time.LocalDate;
import java.util.List;

public class CalendarioDTO {

	private Long camionetaId;
	private String camionetaNombre;
	private String estado;
	private List<OcupacionDTO> ocupaciones;

	public CalendarioDTO() {
	}

	public CalendarioDTO(Long camionetaId, String camionetaNombre, String estado, List<OcupacionDTO> ocupaciones) {
		this.camionetaId = camionetaId;
		this.camionetaNombre = camionetaNombre;
		this.estado = estado;
		this.ocupaciones = ocupaciones;
	}

	public static class OcupacionDTO {
		private Long viajeId;
		private LocalDate fechaInicio;
		private LocalDate fechaFin;
		private Long clienteId;
		private String clienteNombre;
		private String concepto;
		private String estado;

		public OcupacionDTO(Long viajeId, LocalDate fechaInicio, LocalDate fechaFin, Long clienteId, String clienteNombre, String concepto, String estado) {
			this.viajeId = viajeId;
			this.fechaInicio = fechaInicio;
			this.fechaFin = fechaFin;
			this.clienteId = clienteId;
			this.clienteNombre = clienteNombre;
			this.concepto = concepto;
			this.estado = estado;
		}

		public Long getViajeId() { return viajeId; }
		public void setViajeId(Long viajeId) { this.viajeId = viajeId; }
		public LocalDate getFechaInicio() { return fechaInicio; }
		public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
		public LocalDate getFechaFin() { return fechaFin; }
		public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
		public Long getClienteId() { return clienteId; }
		public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
		public String getClienteNombre() { return clienteNombre; }
		public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
		public String getConcepto() { return concepto; }
		public void setConcepto(String concepto) { this.concepto = concepto; }
		public String getEstado() { return estado; }
		public void setEstado(String estado) { this.estado = estado; }
	}

	public Long getCamionetaId() { return camionetaId; }
	public void setCamionetaId(Long camionetaId) { this.camionetaId = camionetaId; }
	public String getCamionetaNombre() { return camionetaNombre; }
	public void setCamionetaNombre(String camionetaNombre) { this.camionetaNombre = camionetaNombre; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }
	public List<OcupacionDTO> getOcupaciones() { return ocupaciones; }
	public void setOcupaciones(List<OcupacionDTO> ocupaciones) { this.ocupaciones = ocupaciones; }

}
