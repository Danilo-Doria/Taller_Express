package com.prueba_desenpeno_java.service;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Cliente;
import java.util.List;

public interface ClienteService {

    Cliente registrar(Cliente cliente) throws PersistenceException;

    Cliente actualizar(Cliente cliente) throws PersistenceException, ClienteNotFoundException;

    Cliente buscarPorId(Integer id) throws PersistenceException, ClienteNotFoundException;

    List<Cliente> listar() throws PersistenceException;

    void validarClienteActivo(Integer idCliente) throws PersistenceException, ClienteNotFoundException;
}
