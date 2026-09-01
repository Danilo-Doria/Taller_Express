package com.prueba_desenpeno_java.controller;

import com.prueba_desenpeno_java.exception.*;
import com.prueba_desenpeno_java.model.*;
import com.prueba_desenpeno_java.service.ClienteService;
import com.prueba_desenpeno_java.service.OrdenServicioService;
import com.prueba_desenpeno_java.service.RepuestoService;
import com.prueba_desenpeno_java.service.VehiculoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdenServicioController {

    private final OrdenServicioService ordenServicioService;
    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;
    private final RepuestoService repuestoService;

    public OrdenServicioController(OrdenServicioService ordenServicioService, ClienteService clienteService,
            VehiculoService vehiculoService, RepuestoService repuestoService) {
        this.ordenServicioService = ordenServicioService;
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
        this.repuestoService = repuestoService;
    }

    public OrdenServicio registrar(
            Integer idCliente, Integer idVehiculo, String mecanico,
            String descripcionProblema,
            String diagnostico, Map<String, Integer> repuestosSolicitados,
            BigDecimal costoManoObra)
            throws PersistenceException, ClienteNotFoundException, VehiculoNotFoundException,
            RepuestoNotFoundException, InsufficientStockException {

        Cliente cliente = clienteService.buscarPorId(idCliente);
        Vehiculo vehiculo = vehiculoService.buscarPorId(idVehiculo);

        List<RepuestoUsado> repuestosUsados = new ArrayList<>();
        for (var entry : repuestosSolicitados.entrySet()) {
            Repuesto repuesto = repuestoService.buscarPorCodigo(entry.getKey());
            repuestosUsados.add(new RepuestoUsado(repuesto, entry.getValue(), repuesto.precioUnitario()));
        }

        OrdenServicio orden = new OrdenServicio(null, cliente, vehiculo, mecanico, LocalDateTime.now(),
                descripcionProblema, diagnostico, repuestosUsados, EstadoOrden.INGRESADO, costoManoObra);

        return ordenServicioService.registrar(orden);
    }

    public OrdenServicio cambiarEstado(Integer idOrden, EstadoOrden nuevoEstado)
            throws PersistenceException, OrderNotFoundException, InvalidOrderStateException {
        return ordenServicioService.cambiarEstado(idOrden, nuevoEstado);
    }

    public OrdenServicio finalizar(Integer idOrden, BigDecimal costoManoObraFinal)
            throws PersistenceException, OrderNotFoundException, InvalidOrderStateException {
        return ordenServicioService.finalizar(idOrden, costoManoObraFinal);
    }

    public List<OrdenServicio> historialPorVehiculo(Integer idVehiculo) throws PersistenceException {
        return ordenServicioService.historialPorVehiculo(idVehiculo);
    }

    public BigDecimal calcularCostoTotal(OrdenServicio orden) {
        return ordenServicioService.calcularCostoTotal(orden);
    }
}
