package com.mexicolindotours.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MantenimientoDTO {
    private Long id;
    private Long camionetaId;
    private String camionetaNombre;
    private LocalDate fecha;
    private Integer kmAlMomento;
    private String tipo;
    private String descripcion;
    private BigDecimal costo;

    public MantenimientoDTO() {}

    public MantenimientoDTO(Long id, Long camionetaId, String camionetaNombre, LocalDate fecha,
                            Integer kmAlMomento, String tipo, String descripcion, BigDecimal costo) {
        this.id = id;
        this.camionetaId = camionetaId;
        this.camionetaNombre = camionetaNombre;
        this.fecha = fecha;
        this.kmAlMomento = kmAlMomento;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.costo = costo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCamionetaId() { return camionetaId; }
    public void setCamionetaId(Long camionetaId) { this.camionetaId = camionetaId; }

    public String getCamionetaNombre() { return camionetaNombre; }
    public void setCamionetaNombre(String camionetaNombre) { this.camionetaNombre = camionetaNombre; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Integer getKmAlMomento() { return kmAlMomento; }
    public void setKmAlMomento(Integer kmAlMomento) { this.kmAlMomento = kmAlMomento; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }
}
