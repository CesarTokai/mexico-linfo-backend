package com.mexicolindotours.dto;

import java.time.LocalDate;

public class DisponibilidadChoferCreateRequest {
    private LocalDate fecha;
    private Boolean disponible;
    private String notas;

    public DisponibilidadChoferCreateRequest() {}

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
