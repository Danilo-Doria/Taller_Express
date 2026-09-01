package com.prueba_desenpeno_java.model;

public record Vehiculo(
        Integer id,
        String placa,
        String marca,
        String modelo,
        int anio,
        String color,
        int idCliente
) {
    public Vehiculo(String placa, String marca, String modelo, int anio, String color, int idCliente) {
        this(null, placa, marca, modelo, anio, color, idCliente);
    }
}