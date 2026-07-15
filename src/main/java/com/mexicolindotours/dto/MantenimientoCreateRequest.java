package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MantenimientoCreateRequest {
    private Long camionetaId;
    private LocalDate fecha;
    private String tipo;
    private BigDecimal costo;
    private String descripcion;

    public MantenimientoCreateRequest() {}

    public Long getCamionetaId() { return camionetaId; }
    public void setCamionetaId(Long camionetaId) { this.camionetaId = camionetaId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
