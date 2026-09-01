package com.prueba_desenpeno_java.repository;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.OrdenServicio;
import java.sql.Connection;
import java.util.List;

public interface OrdenServicioRepository extends CrudRepository<OrdenServicio, Integer> {

    List<OrdenServicio> buscarPorVehiculoId(Integer idVehiculo) throws PersistenceException;

    boolean actualizar(OrdenServicio orden, Connection conn) throws PersistenceException;
}