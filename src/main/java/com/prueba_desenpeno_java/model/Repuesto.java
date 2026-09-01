package com.prueba_desenpeno_java.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Repuesto(
        String codigoReferencia,
        String nombre,
        String categoria,
        String proveedor,
        int stockTotal,
        int stockDisponible,
        BigDecimal precioUnitario,
        boolean isActivo,
        LocalDateTime createdAt) {}
