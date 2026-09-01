package com.prueba_desenpeno_java.service;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.DuplicateVehiclePlacaException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.VehiculoNotFoundException;
import com.prueba_desenpeno_java.model.Vehiculo;
import java.util.List;

public interface VehiculoService {

    Vehiculo registrar(Vehiculo vehiculo) throws PersistenceException, DuplicateVehiclePlacaException, ClienteNotFoundException;

    List<Vehiculo> historialPorCliente(Integer idCliente) throws PersistenceException;

    Vehiculo buscarPorId(Integer id) throws PersistenceException, VehiculoNotFoundException;

    Vehiculo buscarPorPlaca(String placa) throws PersistenceException, VehiculoNotFoundException;
}
