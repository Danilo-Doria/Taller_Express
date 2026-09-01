package com.prueba_desenpeno_java.repository.impl;

import com.prueba_desenpeno_java.config.ConexionBD;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Cliente;
import com.prueba_desenpeno_java.model.EstadoEnum;
import com.prueba_desenpeno_java.repository.ClienteRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryImpl implements ClienteRepository {

    @Override
    public Cliente guardar(Cliente cliente) throws PersistenceException {
        System.out.println("[POST /api/clientes] Registrando cliente: " + cliente.documento());

        String sql = "INSERT INTO clientes (documento, nombre, email, telefono, estado) VALUES (?, ?, ?, ?, ?::estado_enum)";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.documento());
            ps.setString(2, cliente.nombreCompleto());
            ps.setString(3, cliente.email());
            ps.setString(4, cliente.telefono());
            ps.setString(5, cliente.estado().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Cliente(rs.getInt(1), cliente.documento(), cliente.nombreCompleto(),
                            cliente.email(), cliente.telefono(), cliente.estado(), cliente.vehiculos());
                }
            }
            return cliente;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al guardar el cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) throws PersistenceException {
        System.out.println("[GET /api/clientes/" + id + "] Consultando cliente por ID");

        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al buscar cliente por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Cliente> listar() throws PersistenceException {
        System.out.println("[GET /api/clientes] Listando todos los clientes");

        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al obtener clientes: " + e.getMessage(), e);
        }
        return clientes;
    }

    @Override
    public boolean actualizar(Cliente cliente) throws PersistenceException {
        System.out.println("[PUT /api/clientes/" + cliente.id() + "] Actualizando cliente");

        String sql = "UPDATE clientes SET documento = ?, nombre = ?, email = ?, telefono = ?, estado = ?::estado_enum WHERE id_cliente = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.documento());
            ps.setString(2, cliente.nombreCompleto());
            ps.setString(3, cliente.email());
            ps.setString(4, cliente.telefono());
            ps.setString(5, cliente.estado().name());
            ps.setInt(6, cliente.id());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al actualizar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) throws PersistenceException {
        System.out.println("[DELETE /api/clientes/" + id + "] Eliminando cliente");

        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al eliminar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) throws PersistenceException {
        System.out.println("[GET /api/clientes?documento=" + documento + "] Consultando cliente por documento");

        String sql = "SELECT * FROM clientes WHERE documento = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al buscar cliente por documento: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"), rs.getString("documento"), rs.getString("nombre"),
                rs.getString("email"), rs.getString("telefono"),
                EstadoEnum.valueOf(rs.getString("estado")), List.of()
        );
    }
}
