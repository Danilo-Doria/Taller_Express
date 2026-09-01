package com.prueba_desenpeno_java.view;

import com.prueba_desenpeno_java.controller.*;
import com.prueba_desenpeno_java.model.RolEnum;
import com.prueba_desenpeno_java.model.Usuario;

public class MenuPrincipal {

    private final UsuarioView usuarioView;
    private final RepuestoView repuestoView;
    private final ClienteView clienteView;
    private final VehiculoView vehiculoView;
    private final OrdenServicioView ordenServicioView;

    public MenuPrincipal(UsuarioController usuarioController, RepuestoController repuestoController,
            ClienteController clienteController, VehiculoController vehiculoController,
            OrdenServicioController ordenServicioController) {
        this.usuarioView = new UsuarioView(usuarioController);
        this.repuestoView = new RepuestoView(repuestoController);
        this.clienteView = new ClienteView(clienteController, vehiculoController);
        this.vehiculoView = new VehiculoView(vehiculoController);
        this.ordenServicioView = new OrdenServicioView(ordenServicioController);
    }

    public void iniciar() {
        Usuario sesion = usuarioView.login();
        if (sesion == null) {
            DialogHelper.showInfo("Sesión cancelada. Cerrando la aplicación.");
            return;
        }

        boolean esAdmin = sesion.role() == RolEnum.ADMIN;
        String[] opciones = esAdmin
                ? new String[]{"Repuestos", "Clientes", "Vehículos", "Órdenes de Servicio", "Usuarios", "Salir"}
                : new String[]{"Repuestos", "Clientes", "Vehículos", "Órdenes de Servicio", "Salir"};

        boolean salir = false;
        while (!salir) {
            int opcion = DialogHelper.menu("TallerExpress — " + sesion.nombreUsuario() + " (" + sesion.role() + ")", opciones);
            String seleccion = (opcion >= 0 && opcion < opciones.length) ? opciones[opcion] : "Salir";

            switch (seleccion) {
                case "Repuestos" ->
                    repuestoView.mostrarMenu();
                case "Clientes" ->
                    clienteView.mostrarMenu();
                case "Vehículos" ->
                    vehiculoView.mostrarMenu();
                case "Órdenes de Servicio" ->
                    ordenServicioView.mostrarMenu();
                case "Usuarios" ->
                    usuarioView.mostrarMenu(sesion.role());
                default ->
                    salir = true;
            }
        }
    }
}
