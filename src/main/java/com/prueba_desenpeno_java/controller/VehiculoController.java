package com.prueba_desenpeno_java.controller;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.DuplicateVehiclePlacaException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.VehiculoNotFoundException;
import com.prueba_desenpeno_java.model.Vehiculo;
import com.prueba_desenpeno_java.service.VehiculoService;

import java.util.List;

public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    public Vehiculo registrar(String placa, String marca, String modelo, int anio, String color, Integer idCliente)
            throws PersistenceException, DuplicateVehiclePlacaException, ClienteNotFoundException {
        Vehiculo vehiculo = new Vehiculo(placa, marca, modelo, anio, color, idCliente);
        return vehiculoService.registrar(vehiculo);
    }

    public List<Vehiculo> historialPorCliente(Integer idCliente) throws PersistenceException {
        return vehiculoService.historialPorCliente(idCliente);
    }

    public Vehiculo buscarPorPlaca(String placa) throws PersistenceException, VehiculoNotFoundException {
        return vehiculoService.buscarPorPlaca(placa);
    }

    public Vehiculo buscarPorId(Integer id) throws PersistenceException, VehiculoNotFoundException {
        return vehiculoService.buscarPorId(id);
    }
}
