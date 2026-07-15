package com.mexicolindotours.dto;

public class CamionetaCreateRequest {
    private String nombre;
    private String modelo;
    private Integer capacidad;
    private Integer anio;
    private Integer kmActual;
    private String color;

    public CamionetaCreateRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Integer getKmActual() { return kmActual; }
    public void setKmActual(Integer kmActual) { this.kmActual = kmActual; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
