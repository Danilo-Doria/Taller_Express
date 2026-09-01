package com.prueba_desenpeno_java.service;

import com.prueba_desenpeno_java.exception.*;
import com.prueba_desenpeno_java.model.EstadoOrden;
import com.prueba_desenpeno_java.model.OrdenServicio;
import java.math.BigDecimal;
import java.util.List;

public interface OrdenServicioService {

    OrdenServicio registrar(OrdenServicio orden) throws PersistenceException, InsufficientStockException, RepuestoNotFoundException;

    OrdenServicio cambiarEstado(Integer idOrden, EstadoOrden nuevoEstado) throws PersistenceException, OrderNotFoundException, InvalidOrderStateException;

    OrdenServicio finalizar(Integer idOrden, BigDecimal costoManoObraFinal) throws PersistenceException, OrderNotFoundException, InvalidOrderStateException;

    List<OrdenServicio> historialPorVehiculo(Integer idVehiculo) throws PersistenceException;

    BigDecimal calcularCostoTotal(OrdenServicio orden);
}
