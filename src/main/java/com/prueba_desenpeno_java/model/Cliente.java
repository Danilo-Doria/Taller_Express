package com.prueba_desenpeno_java.model;

import java.util.List;

public record Cliente(
        Integer id,
        String documento,
        String nombreCompleto,
        String email,
        String telefono,
        EstadoEnum estado,
        List<Vehiculo> vehiculos
        ) {

    public Cliente(String documento, String nombreCompleto, String email, String telefono, List<Vehiculo> vehiculos) {
        this(null, documento, nombreCompleto, email, telefono, EstadoEnum.ACTIVO, vehiculos);
    }
}
