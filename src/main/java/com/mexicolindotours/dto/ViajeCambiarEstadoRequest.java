package com.mexicolindotours.dto;

public class ViajeCambiarEstadoRequest {
    private String estado;

    public ViajeCambiarEstadoRequest() {}

    public ViajeCambiarEstadoRequest(String estado) {
        this.estado = estado;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
