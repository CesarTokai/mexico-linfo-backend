package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoGeneralDTO {
    private Long id;
    private LocalDate fecha;
    private String descripcion;
    private BigDecimal monto;

    public GastoGeneralDTO() {}

    public GastoGeneralDTO(Long id, LocalDate fecha, String descripcion, BigDecimal monto) {
        this.id = id;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.monto = monto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
}
