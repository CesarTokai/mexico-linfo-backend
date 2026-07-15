package com.mexicolindotours.dto;

public class ViajeFinalizarRequest {
    private Integer kmFinal;

    public ViajeFinalizarRequest() {}

    public ViajeFinalizarRequest(Integer kmFinal) {
        this.kmFinal = kmFinal;
    }

    public Integer getKmFinal() { return kmFinal; }
    public void setKmFinal(Integer kmFinal) { this.kmFinal = kmFinal; }
}
