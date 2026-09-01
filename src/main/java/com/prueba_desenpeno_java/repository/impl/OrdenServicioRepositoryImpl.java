package com.prueba_desenpeno_java.repository.impl;

import com.prueba_desenpeno_java.config.ConexionBD;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Cliente;
import com.prueba_desenpeno_java.model.EstadoOrden;
import com.prueba_desenpeno_java.model.OrdenServicio;
import com.prueba_desenpeno_java.model.Repuesto;
import com.prueba_desenpeno_java.model.RepuestoUsado;
import com.prueba_desenpeno_java.model.Vehiculo;
import com.prueba_desenpeno_java.repository.OrdenServicioRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdenServicioRepositoryImpl implements OrdenServicioRepository {

    private final RepuestoRepositoryImpl repuestoRepository = new RepuestoRepositoryImpl();

    @Override
    public OrdenServicio guardar(OrdenServicio orden) throws PersistenceException {
        System.out.println("[POST /api/ordenes] Registrando orden de servicio para vehículo: " + orden.vehiculo().id());

        String sqlOrden = "INSERT INTO ordenes_servicio (id_cliente, id_vehiculo, mecanico_responsable, fecha_ingreso, descripcion_problema, diagnostico, estado_orden, costo_mano_obra) VALUES (?, ?, ?, ?, ?, ?, ?::estado_orden_enum, ?)";
        String sqlDetalle = "INSERT INTO ordenes_repuestos (id_orden, id_repuesto, cantidad, precio_en_momento) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            int idOrdenGenerado;
            try (PreparedStatement psOrden = conn.prepareStatement(sqlOrden, Statement.RETURN_GENERATED_KEYS)) {
                psOrden.setInt(1, orden.cliente().id());
                psOrden.setInt(2, orden.vehiculo().id());
                psOrden.setString(3, orden.mecanicoResponsable());
                psOrden.setTimestamp(4, Timestamp.valueOf(orden.fechaIngreso()));
                psOrden.setString(5, orden.descripcionProblema());
                psOrden.setString(6, orden.diagnostico());
                psOrden.setString(7, orden.estadoOrden().name());
                psOrden.setBigDecimal(8, orden.costoManoObra());

                psOrden.executeUpdate();

                try (ResultSet rs = psOrden.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("No se pudo obtener el id de la orden generada.");
                    }
                    idOrdenGenerado = rs.getInt(1);
                }
            }

            if (orden.repuestosUtilizados() != null && !orden.repuestosUtilizados().isEmpty()) {
                try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
                    for (RepuestoUsado ru : orden.repuestosUtilizados()) {
                        int idRepuesto = repuestoRepository
                                .obtenerIdPorCodigo(ru.repuesto().codigoReferencia(), conn)
                                .orElseThrow(() -> new SQLException("Repuesto no encontrado: " + ru.repuesto().codigoReferencia()));

                        psDetalle.setInt(1, idOrdenGenerado);
                        psDetalle.setInt(2, idRepuesto);
                        psDetalle.setInt(3, ru.cantidad());
                        psDetalle.setBigDecimal(4, ru.precioEnMomento());
                        psDetalle.addBatch();

                        repuestoRepository.actualizarStock(ru.repuesto().codigoReferencia(), ru.cantidad(), conn);
                    }
                    psDetalle.executeBatch();
                }
            }

            conn.commit();
            System.out.println("[TRANSACCIÓN EXITOSA] Orden #" + idOrdenGenerado + " guardada y stock actualizado.");

            return new OrdenServicio(
                    idOrdenGenerado, orden.cliente(), orden.vehiculo(), orden.mecanicoResponsable(),
                    orden.fechaIngreso(), orden.descripcionProblema(), orden.diagnostico(),
                    orden.repuestosUtilizados(), orden.estadoOrden(), orden.costoManoObra()
            );

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("[TRANSACCIÓN REVERTIDA] Rollback ejecutado debido a un error.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new PersistenceException("Error al guardar la orden de servicio: " + e.getMessage(), e);
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
    public Optional<OrdenServicio> buscarPorId(Integer id) throws PersistenceException {
        System.out.println("[GET /api/ordenes/" + id + "] Consultando orden de servicio");

        String sql = "SELECT * FROM ordenes_servicio WHERE id_orden = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrden(rs, conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al buscar orden por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<OrdenServicio> listar() throws PersistenceException {
        System.out.println("[GET /api/ordenes] Listando todas las órdenes de servicio");

        List<OrdenServicio> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM ordenes_servicio";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ordenes.add(mapResultSetToOrden(rs, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al listar órdenes de servicio: " + e.getMessage(), e);
        }
        return ordenes;
    }

    @Override
    public boolean actualizar(OrdenServicio orden) throws PersistenceException {
        try (Connection conn = ConexionBD.getConnection()) {
            return actualizar(orden, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al actualizar la orden de servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(OrdenServicio orden, Connection conn) throws PersistenceException {
        System.out.println("[PUT /api/ordenes/" + orden.idOrden() + "] Actualizando orden de servicio");

        String sql = "UPDATE ordenes_servicio SET mecanico_responsable = ?, descripcion_problema = ?, diagnostico = ?, estado_orden = ?::estado_orden_enum, costo_mano_obra = ? WHERE id_orden = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orden.mecanicoResponsable());
            ps.setString(2, orden.descripcionProblema());
            ps.setString(3, orden.diagnostico());
            ps.setString(4, orden.estadoOrden().name());
            ps.setBigDecimal(5, orden.costoManoObra());
            ps.setInt(6, orden.idOrden());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al actualizar la orden de servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) throws PersistenceException {
        System.out.println("[DELETE /api/ordenes/" + id + "] Eliminando orden de servicio");
        String sql = "DELETE FROM ordenes_servicio WHERE id_orden = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al eliminar la orden de servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public List<OrdenServicio> buscarPorVehiculoId(Integer idVehiculo) throws PersistenceException {
        System.out.println("[GET /api/vehiculos/" + idVehiculo + "/ordenes] Consultando órdenes por ID de vehículo");

        List<OrdenServicio> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM ordenes_servicio WHERE id_vehiculo = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVehiculo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordenes.add(mapResultSetToOrden(rs, conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al buscar órdenes por vehículo: " + e.getMessage(), e);
        }
        return ordenes;
    }

    private OrdenServicio mapResultSetToOrden(ResultSet rs, Connection conn) throws SQLException {
        int idOrden = rs.getInt("id_orden");
        int clienteId = rs.getInt("id_cliente");
        int vehiculoId = rs.getInt("id_vehiculo");

        // Cliente ahora tiene 7 campos: id, documento, nombreCompleto, email, telefono, estado, vehiculos
        Cliente cliente = new Cliente(clienteId, null, null, null, null, null, null);
        Vehiculo vehiculo = new Vehiculo(vehiculoId, null, null, null, 0, null, clienteId);

        List<RepuestoUsado> repuestos = new ArrayList<>();
        String sqlDetalles = "SELECT r.codigo_referencia, dr.cantidad, dr.precio_en_momento "
                + "FROM ordenes_repuestos dr JOIN repuestos r ON dr.id_repuesto = r.id_repuesto "
                + "WHERE dr.id_orden = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlDetalles)) {
            ps.setInt(1, idOrden);
            try (ResultSet rsDetalles = ps.executeQuery()) {
                while (rsDetalles.next()) {
                    String codigoRef = rsDetalles.getString("codigo_referencia");
                    Repuesto repuestoBasico = new Repuesto(codigoRef, null, null, null, 0, 0, null, false, null);
                    repuestos.add(new RepuestoUsado(
                            repuestoBasico,
                            rsDetalles.getInt("cantidad"),
                            rsDetalles.getBigDecimal("precio_en_momento")
                    ));
                }
            }
        }

        Timestamp fechaIngresoTs = rs.getTimestamp("fecha_ingreso");
        LocalDateTime fechaIngreso = fechaIngresoTs != null ? fechaIngresoTs.toLocalDateTime() : null;

        return new OrdenServicio(
                idOrden, cliente, vehiculo, rs.getString("mecanico_responsable"), fechaIngreso,
                rs.getString("descripcion_problema"), rs.getString("diagnostico"), repuestos,
                EstadoOrden.valueOf(rs.getString("estado_orden")), rs.getBigDecimal("costo_mano_obra")
        );
    }
}
