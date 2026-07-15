package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TramiteVehiculoDTO {
    private Long id;
    private Long camionetaId;
    private String camionetaNombre;
    private String tipo;
    private LocalDate fechaPago;
    private BigDecimal monto;
    private LocalDate fechaVencimiento;
    private String notas;

    public TramiteVehiculoDTO() {}

    public TramiteVehiculoDTO(Long id, Long camionetaId, String camionetaNombre, String tipo,
                              LocalDate fechaPago, BigDecimal monto, LocalDate fechaVencimiento, String notas) {
        this.id = id;
        this.camionetaId = camionetaId;
        this.camionetaNombre = camionetaNombre;
        this.tipo = tipo;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.fechaVencimiento = fechaVencimiento;
        this.notas = notas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCamionetaId() { return camionetaId; }
    public void setCamionetaId(Long camionetaId) { this.camionetaId = camionetaId; }

    public String getCamionetaNombre() { return camionetaNombre; }
    public void setCamionetaNombre(String camionetaNombre) { this.camionetaNombre = camionetaNombre; }

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
