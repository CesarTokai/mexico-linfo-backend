package com.mexicolindotours.dto;

public class ClienteCreateRequest {
    private String nombre;
    private String telefono;
    private String notas;

    public ClienteCreateRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
