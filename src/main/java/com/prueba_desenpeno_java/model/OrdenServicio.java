package com.prueba_desenpeno_java.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenServicio(
        Integer idOrden,
        Cliente cliente,
        Vehiculo vehiculo,
        String mecanicoResponsable,
        LocalDateTime fechaIngreso,
        String descripcionProblema,
        String diagnostico,
        List<RepuestoUsado> repuestosUtilizados,
        EstadoOrden estadoOrden,
        BigDecimal costoManoObra) {

    public OrdenServicio(Cliente cliente, Vehiculo vehiculo, String mecanicoResponsable,
            LocalDateTime fechaIngreso, String descripcionProblema,
            List<RepuestoUsado> repuestosUtilizados, BigDecimal costoManoObra) {

        this(null, cliente, vehiculo, mecanicoResponsable, fechaIngreso, descripcionProblema,
                null, repuestosUtilizados, EstadoOrden.INGRESADO, costoManoObra);
    }
}
