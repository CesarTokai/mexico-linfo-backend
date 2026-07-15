package com.mexicolindotours.dto;

public class UsuarioUpdateRequest {
    private String nombre;
    private String rol;

    public UsuarioUpdateRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
