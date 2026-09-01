package com.prueba_desenpeno_java.repository.impl;

import com.prueba_desenpeno_java.config.ConexionBD;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Vehiculo;
import com.prueba_desenpeno_java.repository.VehiculoRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehiculoRepositoryImpl implements VehiculoRepository {

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) throws PersistenceException {
        System.out.println("[POST /api/vehiculos] Registrando vehículo con placa: " + vehiculo.placa());

        String sql = "INSERT INTO vehiculos (placa, marca, modelo, anio, color, id_cliente) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, vehiculo.placa().trim().toUpperCase());
            ps.setString(2, vehiculo.marca());
            ps.setString(3, vehiculo.modelo());
            ps.setInt(4, vehiculo.anio());
            ps.setString(5, vehiculo.color());
            ps.setInt(6, vehiculo.idCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    return new Vehiculo(
                            idGenerado,
                            vehiculo.placa().trim().toUpperCase(),
                            vehiculo.marca(),
                            vehiculo.modelo(),
                            vehiculo.anio(),
                            vehiculo.color(),
                            vehiculo.idCliente()
                    );
                }
            }
            return vehiculo;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al guardar vehiculo: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Vehiculo> buscarPorId(Integer id) throws PersistenceException {
        System.out.println("[GET /api/vehiculos/" + id + "] Consultando vehículo por ID");

        String sql = "SELECT * FROM vehiculos WHERE id_vehiculo = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVehiculo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al buscar vehiculo por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Vehiculo> listar() throws PersistenceException {
        System.out.println("[GET /api/vehiculos] Listando todos los vehículos");

        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                vehiculos.add(mapResultSetToVehiculo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al listar vehiculos: " + e.getMessage(), e);
        }
        return vehiculos;
    }

    @Override
    public boolean actualizar(Vehiculo vehiculo) throws PersistenceException {
        System.out.println("[PUT /api/vehiculos/" + vehiculo.id() + "] Actualizando datos del vehículo");

        String sql = "UPDATE vehiculos SET placa = ?, marca = ?, modelo = ?, anio = ?, color = ?, id_cliente = ? WHERE id_vehiculo = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehiculo.placa().trim().toUpperCase());
            ps.setString(2, vehiculo.marca());
            ps.setString(3, vehiculo.modelo());
            ps.setInt(4, vehiculo.anio());
            ps.setString(5, vehiculo.color());
            ps.setInt(6, vehiculo.idCliente());
            ps.setInt(7, vehiculo.id());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al actualizar vehiculo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) throws PersistenceException {
        System.out.println("[DELETE /api/vehiculos/" + id + "] Eliminando vehículo");

        String sql = "DELETE FROM vehiculos WHERE id_vehiculo = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al eliminar vehiculo: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Vehiculo> buscarPorPlaca(String placa) throws PersistenceException {
        System.out.println("[GET /api/vehiculos?placa=" + placa + "] Consultando vehículo por placa");
        String sql = "SELECT * FROM vehiculos WHERE UPPER(placa) = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, placa.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVehiculo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al buscar vehiculo por placa: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Vehiculo> buscarPorClienteId(Integer idCliente) throws PersistenceException {
        System.out.println("[GET /api/clientes/" + idCliente + "/vehiculos] Consultando historial de vehículos del cliente");
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos WHERE id_cliente = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapResultSetToVehiculo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al buscar vehiculos del cliente: " + e.getMessage(), e);
        }
        return vehiculos;
    }

    private Vehiculo mapResultSetToVehiculo(ResultSet rs) throws SQLException {
        return new Vehiculo(
                rs.getInt("id_vehiculo"),
                rs.getString("placa"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getInt("anio"),
                rs.getString("color"),
                rs.getInt("id_cliente")
        );
    }

}
