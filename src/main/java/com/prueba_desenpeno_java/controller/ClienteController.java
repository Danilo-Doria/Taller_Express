package com.prueba_desenpeno_java.controller;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Cliente;
import com.prueba_desenpeno_java.service.ClienteService;

import java.util.List;

public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public Cliente registrar(String documento, String nombreCompleto, String email, String telefono)
            throws PersistenceException {

        Cliente cliente = new Cliente(documento, nombreCompleto, email, telefono, List.of());
        return clienteService.registrar(cliente);
    }

    public Cliente actualizar(Integer id, String documento, String nombreCompleto, String email, String telefono)
            throws PersistenceException, ClienteNotFoundException {

        Cliente actual = clienteService.buscarPorId(id);
        Cliente actualizado = new Cliente(id, documento, nombreCompleto, email, telefono, actual.estado(), List.of());
        return clienteService.actualizar(actualizado);
    }

    public List<Cliente> listar() throws PersistenceException {
        return clienteService.listar();
    }

    public Cliente buscarPorId(Integer id) throws PersistenceException, ClienteNotFoundException {
        return clienteService.buscarPorId(id);
    }
}
