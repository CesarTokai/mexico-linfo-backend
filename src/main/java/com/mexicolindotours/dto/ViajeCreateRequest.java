package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ViajeCreateRequest {
    private Long clienteId;
    private Long camionetaId;
    private Long choferId;
    private String concepto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal costoTotal;
    private String notas;

    public ViajeCreateRequest() {}

    public ViajeCreateRequest(Long clienteId, Long camionetaId, Long choferId, String concepto,
                              LocalDate fechaInicio, LocalDate fechaFin, BigDecimal costoTotal, String notas) {
        this.clienteId = clienteId;
        this.camionetaId = camionetaId;
        this.choferId = choferId;
        this.concepto = concepto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costoTotal = costoTotal;
        this.notas = notas;
    }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getCamionetaId() { return camionetaId; }
    public void setCamionetaId(Long camionetaId) { this.camionetaId = camionetaId; }

    public Long getChoferId() { return choferId; }
    public void setChoferId(Long choferId) { this.choferId = choferId; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
