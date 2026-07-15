package com.mexicolindotours.dto;

import java.time.LocalDate;

public class ChoferUpdateRequest {
    private String nombre;
    private String telefono;
    private LocalDate licenciaVencimiento;

    public ChoferUpdateRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getLicenciaVencimiento() { return licenciaVencimiento; }
    public void setLicenciaVencimiento(LocalDate licenciaVencimiento) { this.licenciaVencimiento = licenciaVencimiento; }
}
