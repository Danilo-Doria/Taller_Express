package com.prueba_desenpeno_java.model;

import java.time.LocalDateTime;

public record Usuario(
        Integer id,
        String nombreUsuario,
        String contrasena,
        RolEnum role,
        EstadoEnum estado,
        LocalDateTime createdAt
        ) {

    public Usuario(String nombreUsuario, String contrasena, RolEnum role, EstadoEnum estado) {
        this(null, nombreUsuario, contrasena, role, estado, null);
    }
}
