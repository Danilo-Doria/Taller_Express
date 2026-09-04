package com.prueba_desenpeno_java.view;

import com.prueba_desenpeno_java.controller.ClienteController;
import com.prueba_desenpeno_java.controller.OrdenServicioController;
import com.prueba_desenpeno_java.controller.RepuestoController;
import com.prueba_desenpeno_java.controller.UsuarioController;
import com.prueba_desenpeno_java.controller.VehiculoController;

public class MenuPrincipal {

    private final MainFrame mainFrame;

    public MenuPrincipal(UsuarioController usuarioController, RepuestoController repuestoController,
            ClienteController clienteController, VehiculoController vehiculoController,
            OrdenServicioController ordenServicioController) {

        this.mainFrame = new MainFrame(usuarioController, repuestoController, clienteController,
                vehiculoController, ordenServicioController);
    }

    public void iniciar() {
        mainFrame.iniciar();
    }
}
