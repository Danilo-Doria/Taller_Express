package com.prueba_desenpeno_java.controller;

import com.prueba_desenpeno_java.exception.*;
import com.prueba_desenpeno_java.model.EstadoEnum;
import com.prueba_desenpeno_java.model.RolEnum;
import com.prueba_desenpeno_java.model.Usuario;
import com.prueba_desenpeno_java.service.UsuarioService;

import java.util.List;

public class UsuarioController {

    private final UsuarioService servicioBase;       // respeta el role que le pases
    private final UsuarioService servicioDecorado;    // fuerza RECEPCIONISTA/ACTIVO/now() (decorador)

    public UsuarioController(UsuarioService servicioBase, UsuarioService servicioDecorado) {
        this.servicioBase = servicioBase;
        this.servicioDecorado = servicioDecorado;
    }

    public Usuario registrarUsuario(String nombreUsuario, String contrasena) throws PersistenceException {
        Usuario usuario = new Usuario(nombreUsuario, contrasena, null, null);
        return servicioDecorado.crear(usuario);
    }

    public Usuario registrarAdmin(String nombreUsuario, String contrasena, RolEnum rol, RolEnum rolSolicitante)
            throws PersistenceException, UnauthorizedRoleException {
        if (rolSolicitante != RolEnum.ADMIN) {
            throw new UnauthorizedRoleException("Solo un ADMIN puede crear usuarios con rol específico.");
        }
        Usuario usuario = new Usuario(nombreUsuario, contrasena, rol, EstadoEnum.ACTIVO);
        return servicioBase.crear(usuario);
    }

    public Usuario login(String nombreUsuario, String contrasena)
            throws PersistenceException, InvalidCredentialsException, UserDisabledException {
        return servicioBase.login(nombreUsuario, contrasena);
    }

    public List<Usuario> listar(RolEnum rolSolicitante) throws PersistenceException, UnauthorizedRoleException {
        if (rolSolicitante != RolEnum.ADMIN) {
            throw new UnauthorizedRoleException("Solo un ADMIN puede listar usuarios.");
        }
        return servicioBase.listar();
    }

    public boolean eliminar(Integer id, RolEnum rolSolicitante) throws PersistenceException, UnauthorizedRoleException {
        if (rolSolicitante != RolEnum.ADMIN) {
            throw new UnauthorizedRoleException("Solo un ADMIN puede eliminar usuarios.");
        }
        return servicioBase.eliminar(id);
    }
}
