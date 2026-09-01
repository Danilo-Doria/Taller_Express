package com.prueba_desenpeno_java.repository.impl;

import com.prueba_desenpeno_java.config.ConexionBD;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Repuesto;
import com.prueba_desenpeno_java.repository.RepuestoRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepuestoRepositoryImpl implements RepuestoRepository {

    @Override
    public Repuesto guardar(Repuesto repuesto) throws PersistenceException {
        System.out.println("[POST /api/repuestos] Registrando repuesto con código: " + repuesto.codigoReferencia());

        String sql = "INSERT INTO repuestos (codigo_referencia, nombre, categoria, proveedor, stock_total, stock_disponible, precio_unitario, is_activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, repuesto.codigoReferencia());
            ps.setString(2, repuesto.nombre());
            ps.setString(3, repuesto.categoria());
            ps.setString(4, repuesto.proveedor());
            ps.setInt(5, repuesto.stockTotal());
            ps.setInt(6, repuesto.stockDisponible());
            ps.setBigDecimal(7, repuesto.precioUnitario());
            ps.setBoolean(8, repuesto.isActivo());

            ps.executeUpdate();
            return repuesto;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al guardar repuesto: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Repuesto> buscarPorId(String codigoReferencia) throws PersistenceException {
        System.out.println("[GET /api/repuestos/" + codigoReferencia + "] Consultando repuesto por código");

        String sql = "SELECT * FROM repuestos WHERE codigo_referencia = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoReferencia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRepuesto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al buscar repuesto por código: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Repuesto> listar() throws PersistenceException {
        System.out.println("[GET /api/repuestos] Listando todo el inventario de repuestos");

        List<Repuesto> repuestos = new ArrayList<>();
        String sql = "SELECT * FROM repuestos";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                repuestos.add(mapResultSetToRepuesto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al listar repuestos: " + e.getMessage(), e);
        }
        return repuestos;
    }

    @Override
    public boolean actualizar(Repuesto repuesto) throws PersistenceException {
        System.out.println("[PUT /api/repuestos] Actualizando datos del repuesto: " + repuesto.codigoReferencia());

        String sql = "UPDATE repuestos SET nombre = ?, categoria = ?, proveedor = ?, stock_total = ?, stock_disponible = ?, precio_unitario = ?, is_activo = ? WHERE codigo_referencia = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, repuesto.nombre());
            ps.setString(2, repuesto.categoria());
            ps.setString(3, repuesto.proveedor());
            ps.setInt(4, repuesto.stockTotal());
            ps.setInt(5, repuesto.stockDisponible());
            ps.setBigDecimal(6, repuesto.precioUnitario());
            ps.setBoolean(7, repuesto.isActivo());
            ps.setString(8, repuesto.codigoReferencia());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al actualizar repuesto: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(String codigoReferencia) throws PersistenceException {
        System.out.println("[DELETE /api/repuestos/" + codigoReferencia + "] Eliminando repuesto");

        String sql = "DELETE FROM repuestos WHERE codigo_referencia = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoReferencia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al eliminar repuesto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Repuesto> buscarPorCategoria(String categoria) throws PersistenceException {
        System.out.println("[GET /api/repuestos?categoria=" + categoria + "] Filtrando repuestos por categoría");

        List<Repuesto> repuestos = new ArrayList<>();
        String sql = "SELECT * FROM repuestos WHERE categoria = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    repuestos.add(mapResultSetToRepuesto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al filtrar repuestos por categoría: " + e.getMessage(), e);
        }
        return repuestos;
    }

    @Override
    public List<Repuesto> buscarPorProveedor(String proveedor) throws PersistenceException {
        System.out.println("[GET /api/repuestos?proveedor=" + proveedor + "] Filtrando repuestos por proveedor");

        List<Repuesto> repuestos = new ArrayList<>();
        String sql = "SELECT * FROM repuestos WHERE proveedor = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, proveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    repuestos.add(mapResultSetToRepuesto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al filtrar repuestos por proveedor: " + e.getMessage(), e);
        }
        return repuestos;
    }

    @Override
    public boolean actualizarStock(String codigoReferencia, int cantidadVendida) throws PersistenceException {
        try (Connection conn = ConexionBD.getConnection()) {
            return actualizarStock(codigoReferencia, cantidadVendida, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al actualizar el stock del repuesto: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizarStock(String codigoReferencia, int cantidadVendida, Connection conn) throws PersistenceException {
        System.out.println("[PATCH /api/repuestos/" + codigoReferencia + "/stock] Actualizando stock disponible (restando: " + cantidadVendida + ")");

        String sql = "UPDATE repuestos SET stock_disponible = stock_disponible - ? WHERE codigo_referencia = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidadVendida);
            ps.setString(2, codigoReferencia);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al actualizar el stock del repuesto: " + e.getMessage(), e);
        }
    }

    public Optional<Integer> obtenerIdPorCodigo(String codigoReferencia, Connection conn) throws SQLException {
        String sql = "SELECT id_repuesto FROM repuestos WHERE codigo_referencia = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoReferencia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("id_repuesto"));
                }
            }
        }
        return Optional.empty();
    }

    private Repuesto mapResultSetToRepuesto(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("created_at");
        return new Repuesto(
                rs.getString("codigo_referencia"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getString("proveedor"),
                rs.getInt("stock_total"),
                rs.getInt("stock_disponible"),
                rs.getBigDecimal("precio_unitario"),
                rs.getBoolean("is_activo"),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
