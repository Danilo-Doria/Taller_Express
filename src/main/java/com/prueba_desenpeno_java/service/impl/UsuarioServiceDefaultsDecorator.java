package com.prueba_desenpeno_java.service.impl;

import com.prueba_desenpeno_java.exception.InvalidCredentialsException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.UserDisabledException;
import com.prueba_desenpeno_java.model.EstadoEnum;
import com.prueba_desenpeno_java.model.RolEnum;
import com.prueba_desenpeno_java.model.Usuario;
import com.prueba_desenpeno_java.service.UsuarioService;

import java.time.LocalDateTime;
import java.util.List;

public class UsuarioServiceDefaultsDecorator implements UsuarioService {

    private final UsuarioService usuarioServiceBase;

    public UsuarioServiceDefaultsDecorator(UsuarioService usuarioServiceBase) {
        this.usuarioServiceBase = usuarioServiceBase;
    }

    @Override
    public Usuario crear(Usuario usuario) throws PersistenceException {
        Usuario conDefaults = new Usuario(
                usuario.id(), usuario.nombreUsuario(), usuario.contrasena(),
                usuario.role() != null ? usuario.role() : RolEnum.RECEPCIONISTA,
                EstadoEnum.ACTIVO, LocalDateTime.now()
        );
        System.out.println("[DECORATOR] Defaults aplicados a " + usuario.nombreUsuario()
                + " -> role=" + conDefaults.role() + ", estado=" + conDefaults.estado());
        return usuarioServiceBase.crear(conDefaults);
    }

    @Override
    public Usuario login(String nombreUsuario, String contrasena) throws PersistenceException, InvalidCredentialsException, UserDisabledException {
        return usuarioServiceBase.login(nombreUsuario, contrasena);
    }

    @Override
    public List<Usuario> listar() throws PersistenceException {
        return usuarioServiceBase.listar();
    }

    @Override
    public Usuario actualizar(Usuario usuario) throws PersistenceException {
        return usuarioServiceBase.actualizar(usuario);
    }

    @Override
    public boolean eliminar(Integer id) throws PersistenceException {
        return usuarioServiceBase.eliminar(id);
    }
}
