package com.prueba_desenpeno_java.model;

import java.math.BigDecimal;

public record RepuestoUsado(
        Repuesto repuesto,
        int cantidad,
        BigDecimal precioEnMomento) {}