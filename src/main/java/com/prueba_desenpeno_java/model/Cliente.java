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
}
