package com.prueba_desenpeno_java.repository;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {

    Optional<Usuario> buscarPorUsuario(String usuario) throws PersistenceException;
}
