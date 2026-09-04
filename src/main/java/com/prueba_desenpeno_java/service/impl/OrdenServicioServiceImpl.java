package com.prueba_desenpeno_java.service.impl;

import com.prueba_desenpeno_java.config.ConexionBD;
import com.prueba_desenpeno_java.exception.*;
import com.prueba_desenpeno_java.model.EstadoOrden;
import com.prueba_desenpeno_java.model.OrdenServicio;
import com.prueba_desenpeno_java.model.Repuesto;
import com.prueba_desenpeno_java.model.RepuestoUsado;
import com.prueba_desenpeno_java.repository.OrdenServicioRepository;
import com.prueba_desenpeno_java.repository.RepuestoRepository;
import com.prueba_desenpeno_java.repository.impl.OrdenServicioRepositoryImpl;
import com.prueba_desenpeno_java.repository.impl.RepuestoRepositoryImpl;
import com.prueba_desenpeno_java.service.OrdenServicioService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class OrdenServicioServiceImpl implements OrdenServicioService {

    private final OrdenServicioRepository ordenRepository;
    private final RepuestoRepository repuestoRepository;

    private static final Map<EstadoOrden, EnumSet<EstadoOrden>> TRANSICIONES_VALIDAS = new EnumMap<>(EstadoOrden.class);

    static {
        TRANSICIONES_VALIDAS.put(EstadoOrden.INGRESADO, EnumSet.of(EstadoOrden.DIAGNOSTICO));
        TRANSICIONES_VALIDAS.put(EstadoOrden.DIAGNOSTICO, EnumSet.of(EstadoOrden.EN_REPARACION));
        TRANSICIONES_VALIDAS.put(EstadoOrden.EN_REPARACION, EnumSet.of(EstadoOrden.LISTO));
        TRANSICIONES_VALIDAS.put(EstadoOrden.LISTO, EnumSet.of(EstadoOrden.ENTREGADO));
        TRANSICIONES_VALIDAS.put(EstadoOrden.ENTREGADO, EnumSet.noneOf(EstadoOrden.class));
    }

    public OrdenServicioServiceImpl() {
        this.ordenRepository = new OrdenServicioRepositoryImpl();
        this.repuestoRepository = new RepuestoRepositoryImpl();
    }

    public OrdenServicioServiceImpl(OrdenServicioRepository ordenRepository, RepuestoRepository repuestoRepository) {
        this.ordenRepository = ordenRepository;
        this.repuestoRepository = repuestoRepository;
    }

    @Override
    public OrdenServicio registrar(OrdenServicio orden) throws PersistenceException, InsufficientStockException, RepuestoNotFoundException {
        if (orden.repuestosUtilizados() != null) {
            for (RepuestoUsado ru : orden.repuestosUtilizados()) {
                Repuesto repuesto = repuestoRepository.buscarPorId(ru.repuesto().codigoReferencia())
                        .orElseThrow(() -> new RepuestoNotFoundException("Repuesto no encontrado: " + ru.repuesto().codigoReferencia()));

                if (repuesto.stockDisponible() < ru.cantidad()) {
                    throw new InsufficientStockException("Stock insuficiente para " + repuesto.codigoReferencia()
                            + " (disponible: " + repuesto.stockDisponible() + ", solicitado: " + ru.cantidad() + ")");
                }
            }
        }
        return ordenRepository.guardar(orden); // ya es transaccional dentro del repositorio
    }

    @Override
    public OrdenServicio cambiarEstado(Integer idOrden, EstadoOrden nuevoEstado) throws PersistenceException, OrderNotFoundException, InvalidOrderStateException {
        OrdenServicio orden = buscarOrdenOrFail(idOrden);
        validarTransicion(orden.estadoOrden(), nuevoEstado);

        OrdenServicio actualizada = conNuevoEstado(orden, nuevoEstado);
        if (!ordenRepository.actualizar(actualizada)) {
            throw new PersistenceException("No se pudo actualizar el estado de la orden: " + idOrden);
        }
        return actualizada;
    }

    @Override
    public OrdenServicio finalizar(Integer idOrden, BigDecimal costoManoObraFinal) throws PersistenceException, OrderNotFoundException, InvalidOrderStateException {
        OrdenServicio orden = buscarOrdenOrFail(idOrden);
        validarTransicion(orden.estadoOrden(), EstadoOrden.ENTREGADO);

        OrdenServicio finalizada = new OrdenServicio(orden.idOrden(), orden.cliente(), orden.vehiculo(),
                orden.mecanicoResponsable(), orden.fechaIngreso(), orden.descripcionProblema(),
                orden.diagnostico(), orden.repuestosUtilizados(), EstadoOrden.ENTREGADO, costoManoObraFinal);

        Connection conn = null;
        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            if (!ordenRepository.actualizar(finalizada, conn)) {
                throw new SQLException("No se pudo actualizar la orden " + idOrden);
            }

            conn.commit();

            System.out.println("[TRANSACCIÓN EXITOSA] Orden #" + idOrden + " finalizada.");
            return finalizada;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("[TRANSACCIÓN REVERTIDA] Rollback al finalizar orden #" + idOrden);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new PersistenceException("Error al finalizar la orden: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<OrdenServicio> historialPorVehiculo(Integer idVehiculo) throws PersistenceException {
        return ordenRepository.buscarPorVehiculoId(idVehiculo);
    }

    @Override
    public BigDecimal calcularCostoTotal(OrdenServicio orden) {
        BigDecimal totalRepuestos = orden.repuestosUtilizados() == null ? BigDecimal.ZERO
                : orden.repuestosUtilizados().stream()
                        .map(ru -> ru.precioEnMomento().multiply(BigDecimal.valueOf(ru.cantidad())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal manoObra = orden.costoManoObra() != null ? orden.costoManoObra() : BigDecimal.ZERO;
        return totalRepuestos.add(manoObra);
    }

    private OrdenServicio buscarOrdenOrFail(Integer idOrden) throws PersistenceException, OrderNotFoundException {
        return ordenRepository.buscarPorId(idOrden)
                .orElseThrow(() -> new OrderNotFoundException("No existe la orden de servicio: " + idOrden));
    }

    private void validarTransicion(EstadoOrden actual, EstadoOrden nuevo) throws InvalidOrderStateException {
        EnumSet<EstadoOrden> permitidos = TRANSICIONES_VALIDAS.get(actual);
        if (permitidos == null || !permitidos.contains(nuevo)) {
            throw new InvalidOrderStateException("No se puede pasar de " + actual + " a " + nuevo + ".");
        }
    }

    private OrdenServicio conNuevoEstado(OrdenServicio orden, EstadoOrden nuevoEstado) {
        return new OrdenServicio(orden.idOrden(), orden.cliente(), orden.vehiculo(), orden.mecanicoResponsable(),
                orden.fechaIngreso(), orden.descripcionProblema(), orden.diagnostico(),
                orden.repuestosUtilizados(), nuevoEstado, orden.costoManoObra());
    }
}
