package com.prueba_desenpeno_java;

import com.prueba_desenpeno_java.controller.*;
import com.prueba_desenpeno_java.repository.impl.*;
import com.prueba_desenpeno_java.service.*;
import com.prueba_desenpeno_java.service.impl.*;
import com.prueba_desenpeno_java.view.MenuPrincipal;

public class Prueba_desenpeno_java {

    public static void main(String[] args) {
        // Repositories
        var clienteRepository = new ClienteRepositoryImpl();
        var vehiculoRepository = new VehiculoRepositoryImpl();
        var repuestoRepository = new RepuestoRepositoryImpl();
        var usuarioRepository = new UsuarioRepositoryImpl();
        var ordenServicioRepository = new OrdenServicioRepositoryImpl();

        // Services
        ClienteService clienteService = new ClienteServiceImpl(clienteRepository);
        VehiculoService vehiculoService = new VehiculoServiceImpl(vehiculoRepository, clienteService);
        RepuestoService repuestoService = new RepuestoServiceImpl(repuestoRepository);
        UsuarioService usuarioServiceBase = new UsuarioServiceImpl(usuarioRepository);
        UsuarioService usuarioServiceDecorado = new UsuarioServiceDefaultsDecorator(usuarioServiceBase);
        OrdenServicioService ordenServicioService = new OrdenServicioServiceImpl(ordenServicioRepository, repuestoRepository);

        // Controllers
        ClienteController clienteController = new ClienteController(clienteService);
        VehiculoController vehiculoController = new VehiculoController(vehiculoService);
        RepuestoController repuestoController = new RepuestoController(repuestoService);
        UsuarioController usuarioController = new UsuarioController(usuarioServiceBase, usuarioServiceDecorado);
        OrdenServicioController ordenServicioController = new OrdenServicioController(
                ordenServicioService, clienteService, vehiculoService, repuestoService);

        // View
        new MenuPrincipal(usuarioController, repuestoController, clienteController,
                vehiculoController, ordenServicioController).iniciar();
    }
}
