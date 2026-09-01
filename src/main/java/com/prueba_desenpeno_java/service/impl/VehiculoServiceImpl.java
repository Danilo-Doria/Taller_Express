package com.prueba_desenpeno_java.service.impl;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.DuplicateVehiclePlacaException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.VehiculoNotFoundException;
import com.prueba_desenpeno_java.model.Vehiculo;
import com.prueba_desenpeno_java.repository.VehiculoRepository;
import com.prueba_desenpeno_java.repository.impl.VehiculoRepositoryImpl;
import com.prueba_desenpeno_java.service.ClienteService;
import com.prueba_desenpeno_java.service.VehiculoService;

import java.util.List;

public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteService clienteService;

    public VehiculoServiceImpl(ClienteService clienteService) {
        this.vehiculoRepository = new VehiculoRepositoryImpl();
        this.clienteService = clienteService;
    }

    public VehiculoServiceImpl(VehiculoRepository vehiculoRepository, ClienteService clienteService) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteService = clienteService;
    }

    @Override
    public Vehiculo registrar(Vehiculo vehiculo) throws PersistenceException, DuplicateVehiclePlacaException, ClienteNotFoundException {
        clienteService.validarClienteActivo(vehiculo.idCliente());

        if (vehiculoRepository.buscarPorPlaca(vehiculo.placa()).isPresent()) {
            throw new DuplicateVehiclePlacaException("Ya existe un vehículo con la placa: " + vehiculo.placa());
        }
        return vehiculoRepository.guardar(vehiculo);
    }

    @Override
    public List<Vehiculo> historialPorCliente(Integer idCliente) throws PersistenceException {
        return vehiculoRepository.buscarPorClienteId(idCliente);
    }

    @Override
    public Vehiculo buscarPorId(Integer id) throws PersistenceException, VehiculoNotFoundException {
        return vehiculoRepository.buscarPorId(id)
                .orElseThrow(() -> new VehiculoNotFoundException("Vehículo no encontrado: " + id));
    }

    @Override
    public Vehiculo buscarPorPlaca(String placa) throws PersistenceException, VehiculoNotFoundException {
        return vehiculoRepository.buscarPorPlaca(placa)
                .orElseThrow(() -> new VehiculoNotFoundException("Vehículo no encontrado con placa: " + placa));
    }
}
