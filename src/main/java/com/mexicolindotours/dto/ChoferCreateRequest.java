package com.mexicolindotours.dto;

import java.time.LocalDate;

public class ChoferCreateRequest {
    private String nombre;
    private String telefono;
    private String licenciaNumero;
    private LocalDate licenciaVencimiento;

    public ChoferCreateRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getLicenciaNumero() { return licenciaNumero; }
    public void setLicenciaNumero(String licenciaNumero) { this.licenciaNumero = licenciaNumero; }

    public LocalDate getLicenciaVencimiento() { return licenciaVencimiento; }
    public void setLicenciaVencimiento(LocalDate licenciaVencimiento) { this.licenciaVencimiento = licenciaVencimiento; }
}
