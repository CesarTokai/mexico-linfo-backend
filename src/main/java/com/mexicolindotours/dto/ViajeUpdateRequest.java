package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ViajeUpdateRequest {
    private Long choferId;
    private String concepto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer kmInicial;
    private BigDecimal costoTotal;
    private String notas;

    public ViajeUpdateRequest() {}

    public Long getChoferId() { return choferId; }
    public void setChoferId(Long choferId) { this.choferId = choferId; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getKmInicial() { return kmInicial; }
    public void setKmInicial(Integer kmInicial) { this.kmInicial = kmInicial; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
