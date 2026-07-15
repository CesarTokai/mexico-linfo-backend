package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TramiteVehiculoCreateRequest {
    private Long camionetaId;
    private String tipo;
    private LocalDate fechaPago;
    private BigDecimal monto;
    private LocalDate fechaVencimiento;
    private String notas;

    public TramiteVehiculoCreateRequest() {}

    public Long getCamionetaId() { return camionetaId; }
    public void setCamionetaId(Long camionetaId) { this.camionetaId = camionetaId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
