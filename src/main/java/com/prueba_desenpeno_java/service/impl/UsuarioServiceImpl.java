package com.prueba_desenpeno_java.service.impl;

import com.prueba_desenpeno_java.exception.InvalidCredentialsException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.UserDisabledException;
import com.prueba_desenpeno_java.model.EstadoEnum;
import com.prueba_desenpeno_java.model.Usuario;
import com.prueba_desenpeno_java.repository.UsuarioRepository;
import com.prueba_desenpeno_java.repository.impl.UsuarioRepositoryImpl;
import com.prueba_desenpeno_java.service.UsuarioService;

import java.util.List;

public class UsuarioServiceImpl implements UsuarioService {

    protected final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl() {
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario crear(Usuario usuario) throws PersistenceException {

        System.out.println("[POST /api/usuarios] (lógica base) creando usuario: " + usuario.nombreUsuario());
        return usuarioRepository.guardar(usuario);
    }

    @Override
    public Usuario login(String nombreUsuario, String contrasena) throws PersistenceException, InvalidCredentialsException, UserDisabledException {
        Usuario usuario = usuarioRepository.buscarPorUsuario(nombreUsuario)
                .orElseThrow(() -> new InvalidCredentialsException("Usuario o contraseña incorrectos."));

        if (!usuario.contrasena().equals(contrasena)) {
            throw new InvalidCredentialsException("Usuario o contraseña incorrectos.");
        }
        if (usuario.estado() != EstadoEnum.ACTIVO) {
            throw new UserDisabledException("El usuario " + nombreUsuario + " está inactivo.");
        }
        return usuario;
    }

    @Override
    public List<Usuario> listar() throws PersistenceException {
        return usuarioRepository.listar();
    }

    @Override
    public Usuario actualizar(Usuario usuario) throws PersistenceException {
        usuarioRepository.actualizar(usuario);
        return usuario;
    }

    @Override
    public boolean eliminar(Integer id) throws PersistenceException {
        return usuarioRepository.eliminar(id);
    }
}
