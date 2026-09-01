package com.prueba_desenpeno_java.service;

import com.prueba_desenpeno_java.exception.InvalidCredentialsException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.UserDisabledException;
import com.prueba_desenpeno_java.model.Usuario;
import java.util.List;

public interface UsuarioService {

    Usuario crear(Usuario usuario) throws PersistenceException;

    Usuario login(String nombreUsuario, String contrasena) throws PersistenceException, InvalidCredentialsException, UserDisabledException;

    List<Usuario> listar() throws PersistenceException;

    Usuario actualizar(Usuario usuario) throws PersistenceException;

    boolean eliminar(Integer id) throws PersistenceException;
}
