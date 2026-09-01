package com.prueba_desenpeno_java.repository;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Vehiculo;
import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends CrudRepository<Vehiculo, Integer> {

    Optional<Vehiculo> buscarPorPlaca(String placa) throws PersistenceException;

    List<Vehiculo> buscarPorClienteId(Integer idCliente) throws PersistenceException;
}
