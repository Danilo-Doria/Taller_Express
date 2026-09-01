package com.prueba_desenpeno_java.view;

import com.prueba_desenpeno_java.controller.VehiculoController;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Vehiculo;

public class VehiculoView {

    private final VehiculoController vehiculoController;

    public VehiculoView(VehiculoController vehiculoController) {
        this.vehiculoController = vehiculoController;
    }

    public void mostrarMenu() {
        String[] opciones = {"Registrar vehículo", "Buscar por placa", "Volver"};
        boolean salir = false;
        while (!salir) {
            int opcion = DialogHelper.menu("Gestión de Vehículos", opciones);
            try {
                switch (opcion) {
                    case 0 ->
                        registrar();
                    case 1 ->
                        buscarPorPlaca();
                    default ->
                        salir = true;
                }
            } catch (PersistenceException e) {
                DialogHelper.showError("Error de persistencia: " + e.getMessage());
            } catch (RuntimeException e) {
                DialogHelper.showError(e.getMessage());
            }
        }
    }

    private void registrar() throws PersistenceException {
        String placa = DialogHelper.input("Placa:");
        if (placa == null) {
            return;
        }
        String marca = DialogHelper.input("Marca:");
        String modelo = DialogHelper.input("Modelo:");
        int anio = Integer.parseInt(DialogHelper.input("Año:"));
        String color = DialogHelper.input("Color:");
        Integer idCliente = Integer.valueOf(DialogHelper.input("Id del cliente dueño:"));

        Vehiculo creado = vehiculoController.registrar(placa, marca, modelo, anio, color, idCliente);
        DialogHelper.showSuccess("Vehículo registrado con id: " + creado.id());
    }

    private void buscarPorPlaca() throws PersistenceException {
        String placa = DialogHelper.input("Placa a buscar:");
        if (placa == null) {
            return;
        }
        Vehiculo v = vehiculoController.buscarPorPlaca(placa);
        DialogHelper.showInfo("Id: " + v.id() + "\nPlaca: " + v.placa() + "\nMarca: " + v.marca()
                + "\nModelo: " + v.modelo() + "\nAño: " + v.anio() + "\nColor: " + v.color()
                + "\nCliente id: " + v.idCliente());
    }
}
