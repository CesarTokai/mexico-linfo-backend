package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoCreateRequest {
    private String tipo;
    private LocalDate fecha;
    private BigDecimal monto;
    private String notas;

    public GastoCreateRequest() {}

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
