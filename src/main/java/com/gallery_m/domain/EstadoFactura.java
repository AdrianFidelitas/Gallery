package com.gallery_m.domain;

public enum EstadoFactura {
    Activa("Activa"),
    Pagada("Pagada"),
    Anulada("Anulada");

    private final String valorBD;

    EstadoFactura(String valorBD) {
        this.valorBD = valorBD;
    }

    public String getValorBD() {
        return valorBD;
    }
    
    @Override
    public String toString() {
        return valorBD;
    }
}