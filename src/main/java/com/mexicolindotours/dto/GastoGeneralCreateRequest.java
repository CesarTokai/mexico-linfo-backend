package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoGeneralCreateRequest {
    private LocalDate fecha;
    private String descripcion;
    private BigDecimal monto;

    public GastoGeneralCreateRequest() {}

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
}
