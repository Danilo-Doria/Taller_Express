package com.prueba_desenpeno_java.repository;

import com.prueba_desenpeno_java.exception.PersistenceException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {

    T guardar(T entity) throws PersistenceException;

    Optional<T> buscarPorId(ID id) throws PersistenceException;

    List<T> listar() throws PersistenceException;

    boolean actualizar(T entity) throws PersistenceException;

    boolean eliminar(ID id) throws PersistenceException;
}