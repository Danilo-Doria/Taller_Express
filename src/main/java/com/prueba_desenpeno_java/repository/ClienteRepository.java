package com.prueba_desenpeno_java.repository;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Cliente;
import java.util.Optional;

public interface ClienteRepository extends CrudRepository<Cliente, Integer> {

    Optional<Cliente> buscarPorDocumento(String documento) throws PersistenceException;
}
