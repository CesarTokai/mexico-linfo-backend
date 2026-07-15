package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoCreateRequest {
    private String tipo;
    private LocalDate fechaPago;
    private BigDecimal monto;
    private String metodo;
    private String notas;

    public PagoCreateRequest() {}

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
