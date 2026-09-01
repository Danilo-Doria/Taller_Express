package com.prueba_desenpeno_java.view;

import com.prueba_desenpeno_java.controller.ClienteController;
import com.prueba_desenpeno_java.controller.VehiculoController;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Cliente;
import com.prueba_desenpeno_java.model.EstadoEnum;
import com.prueba_desenpeno_java.model.Vehiculo;

import java.util.ArrayList;
import java.util.List;

public class ClienteView {

    private final ClienteController clienteController;
    private final VehiculoController vehiculoController;

    public ClienteView(ClienteController clienteController, VehiculoController vehiculoController) {
        this.clienteController = clienteController;
        this.vehiculoController = vehiculoController;
    }

    public void mostrarMenu() {
        String[] opciones = {"Registrar cliente", "Editar cliente", "Listar clientes",
            "Ver historial de vehículos por cliente", "Volver"};
        boolean salir = false;
        while (!salir) {
            int opcion = DialogHelper.menu("Gestión de Clientes", opciones);
            try {
                switch (opcion) {
                    case 0 ->
                        registrar();
                    case 1 ->
                        editar();
                    case 2 ->
                        listar(clienteController.listar());
                    case 3 ->
                        verHistorialVehiculos();
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
        String documento = DialogHelper.input("Documento:");
        if (documento == null) {
            return;
        }
        String nombre = DialogHelper.input("Nombre completo:");
        String email = DialogHelper.input("Email:");
        String telefono = DialogHelper.input("Teléfono:");

        Cliente creado = clienteController.registrar(documento, nombre, email, telefono);
        DialogHelper.showSuccess("Cliente registrado con id: " + creado.id());
    }

    private void editar() throws PersistenceException {
        Integer id = Integer.valueOf(DialogHelper.input("Id del cliente a editar:"));
        Cliente actual = clienteController.buscarPorId(id);

        String documento = DialogHelper.input("Documento:", actual.documento());
        String nombre = DialogHelper.input("Nombre completo:", actual.nombreCompleto());
        String email = DialogHelper.input("Email:", actual.email());
        String telefono = DialogHelper.input("Teléfono:", actual.telefono());

        clienteController.actualizar(id, documento, nombre, email, telefono);
        DialogHelper.showSuccess("Cliente actualizado: " + id);
    }

    private void verHistorialVehiculos() throws PersistenceException {
        Integer id = Integer.valueOf(DialogHelper.input("Id del cliente:"));
        List<Vehiculo> vehiculos = vehiculoController.historialPorCliente(id);

        String[] headers = {"Id", "Placa", "Marca", "Modelo", "Año", "Color"};
        List<String[]> filas = new ArrayList<>();
        for (Vehiculo v : vehiculos) {
            filas.add(new String[]{String.valueOf(v.id()), v.placa(), v.marca(), v.modelo(),
                String.valueOf(v.anio()), v.color()});
        }
        DialogHelper.showInfo(DialogHelper.formatTable(headers, filas));
    }

    private void listar(List<Cliente> clientes) {
        String[] headers = {"Id", "Documento", "Nombre", "Email", "Teléfono", "Estado"};
        List<String[]> filas = new ArrayList<>();
        for (Cliente c : clientes) {
            filas.add(new String[]{String.valueOf(c.id()), c.documento(), c.nombreCompleto(), c.email(),
                c.telefono(), DialogHelper.etiquetaEstado(c.estado() == EstadoEnum.ACTIVO)});
        }
        DialogHelper.showInfo(DialogHelper.formatTable(headers, filas));
    }
}
