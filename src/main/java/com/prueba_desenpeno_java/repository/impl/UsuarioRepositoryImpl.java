package com.prueba_desenpeno_java.repository.impl;

import com.prueba_desenpeno_java.config.ConexionBD;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.EstadoEnum;
import com.prueba_desenpeno_java.model.RolEnum;
import com.prueba_desenpeno_java.model.Usuario;
import com.prueba_desenpeno_java.repository.UsuarioRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepositoryImpl implements UsuarioRepository {

    @Override
    public Usuario guardar(Usuario usuario) throws PersistenceException {
        System.out.println("[POST /api/usuarios] Registrando usuario: " + usuario.nombreUsuario());

        String sql = "INSERT INTO usuarios (username, password, role, estado) VALUES (?, ?, ?::rol_enum, ?::estado_enum)";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.nombreUsuario());
            ps.setString(2, usuario.contrasena());
            ps.setString(3, usuario.role().name());
            ps.setString(4, usuario.estado().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);

                    return new Usuario(
                            idGenerado,
                            usuario.nombreUsuario(),
                            usuario.contrasena(),
                            usuario.role(),
                            usuario.estado(),
                            LocalDateTime.now()
                    );
                }
            }
            return usuario;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al guardar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) throws PersistenceException {
        System.out.println("[GET /api/usuarios/" + id + "] Consultando usuario por ID");

        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // CORREGIDO: Se cambió RuntimeException por PersistenceException
            throw new PersistenceException("Error al buscar usuario por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> listar() throws PersistenceException {
        System.out.println("[GET /api/usuarios] Listando todos los usuarios");

        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al obtener usuarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    @Override
    public boolean actualizar(Usuario usuario) throws PersistenceException {
        System.out.println("[PUT /api/usuarios/" + usuario.id() + "] Actualizando datos de usuario");

        String sql = "UPDATE usuarios SET username = ?, password = ?, role = ?::rol_enum, estado = ?::estado_enum WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.nombreUsuario());
            ps.setString(2, usuario.contrasena());
            ps.setString(3, usuario.role().name());
            ps.setString(4, usuario.estado().name());
            ps.setInt(5, usuario.id());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) throws PersistenceException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorUsuario(String usuario) throws PersistenceException {
        System.out.println("[GET /api/usuarios?username=" + usuario + "] Consultando usuario por Username");

        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenceException("Error al buscar usuario por username: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("username"),
                rs.getString("password"),
                RolEnum.valueOf(rs.getString("role")),
                EstadoEnum.valueOf(rs.getString("estado")),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

}
