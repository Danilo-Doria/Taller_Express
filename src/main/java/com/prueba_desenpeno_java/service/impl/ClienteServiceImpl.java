package com.prueba_desenpeno_java.service.impl;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Cliente;
import com.prueba_desenpeno_java.model.EstadoEnum;
import com.prueba_desenpeno_java.repository.ClienteRepository;
import com.prueba_desenpeno_java.repository.impl.ClienteRepositoryImpl;
import com.prueba_desenpeno_java.service.ClienteService;

import java.util.List;

public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl() {
        this.clienteRepository = new ClienteRepositoryImpl();
    }

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente registrar(Cliente cliente) throws PersistenceException {
        Cliente nuevo = new Cliente(null, cliente.documento(), cliente.nombreCompleto(),
                cliente.email(), cliente.telefono(), EstadoEnum.ACTIVO, List.of());
        return clienteRepository.guardar(nuevo);
    }

    @Override
    public Cliente actualizar(Cliente cliente) throws PersistenceException, ClienteNotFoundException {
        buscarPorId(cliente.id());
        if (!clienteRepository.actualizar(cliente)) {
            throw new PersistenceException("No se pudo actualizar el cliente: " + cliente.id());
        }
        return cliente;
    }

    @Override
    public Cliente buscarPorId(Integer id) throws PersistenceException, ClienteNotFoundException {
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new ClienteNotFoundException("No existe cliente con id: " + id));
    }

    @Override
    public List<Cliente> listar() throws PersistenceException {
        return clienteRepository.listar();
    }

    @Override
    public void validarClienteActivo(Integer idCliente) throws PersistenceException, ClienteNotFoundException {
        Cliente cliente = buscarPorId(idCliente);
        if (cliente.estado() != EstadoEnum.ACTIVO) {
            throw new ClienteNotFoundException("El cliente " + idCliente + " está inactivo.");
        }
    }
}
