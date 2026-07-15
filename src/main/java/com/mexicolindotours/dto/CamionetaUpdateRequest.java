package com.mexicolindotours.dto;

public class CamionetaUpdateRequest {
    private String nombre;
    private String modelo;
    private Integer capacidad;
    private String estado;
    private Integer kmActual;
    private Integer kmMantenimiento;

    public CamionetaUpdateRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getKmActual() { return kmActual; }
    public void setKmActual(Integer kmActual) { this.kmActual = kmActual; }

    public Integer getKmMantenimiento() { return kmMantenimiento; }
    public void setKmMantenimiento(Integer kmMantenimiento) { this.kmMantenimiento = kmMantenimiento; }
}
