package com.prueba_desenpeno_java.view;

import com.prueba_desenpeno_java.controller.OrdenServicioController;
import com.prueba_desenpeno_java.exception.*;
import com.prueba_desenpeno_java.model.EstadoOrden;
import com.prueba_desenpeno_java.model.OrdenServicio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrdenServicioView {

    private final OrdenServicioController ordenServicioController;

    public OrdenServicioView(OrdenServicioController ordenServicioController) {
        this.ordenServicioController = ordenServicioController;
    }

    public void mostrarMenu() {
        String[] opciones = {"Registrar orden de servicio", "Cambiar estado de orden", "Finalizar orden",
            "Historial por vehículo", "Volver"};
        boolean salir = false;
        while (!salir) {
            int opcion = DialogHelper.menu("Órdenes de Servicio", opciones);
            try {
                switch (opcion) {
                    case 0 ->
                        registrar();
                    case 1 ->
                        cambiarEstado();
                    case 2 ->
                        finalizar();
                    case 3 ->
                        historialPorVehiculo();
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
        Integer idCliente = Integer.valueOf(DialogHelper.input("Id del cliente:"));
        Integer idVehiculo = Integer.valueOf(DialogHelper.input("Id del vehículo:"));
        String mecanico = DialogHelper.input("Mecánico responsable:");
        String descripcion = DialogHelper.input("Descripción del problema:");
        String diagnostico = DialogHelper.input("Diagnóstico (opcional):");
        BigDecimal costoManoObra = new BigDecimal(DialogHelper.input("Costo de mano de obra:", "0.00"));

        Map<String, Integer> repuestos = new LinkedHashMap<>();
        while (DialogHelper.confirm("¿Agregar un repuesto a la orden?")) {
            String codigo = DialogHelper.input("Código del repuesto:");
            if (codigo == null) {
                break;
            }
            int cantidad = Integer.parseInt(DialogHelper.input("Cantidad:", "1"));
            repuestos.put(codigo, cantidad);
        }

        OrdenServicio orden = ordenServicioController.registrar(idCliente, idVehiculo, mecanico, descripcion,
                diagnostico, repuestos, costoManoObra);
        BigDecimal total = ordenServicioController.calcularCostoTotal(orden);
        DialogHelper.showSuccess("Orden #" + orden.idOrden() + " registrada. Costo total estimado: " + total);
    }

    private void cambiarEstado() throws PersistenceException, OrderNotFoundException, InvalidOrderStateException {
        Integer idOrden = Integer.valueOf(DialogHelper.input("Id de la orden:"));
        String[] estados = {"DIAGNOSTICO", "EN_REPARACION", "LISTO", "ENTREGADO"};
        int idx = DialogHelper.menu("Nuevo estado", estados);
        if (idx < 0) {
            return;
        }
        OrdenServicio actualizada = ordenServicioController.cambiarEstado(idOrden, EstadoOrden.valueOf(estados[idx]));
        DialogHelper.showSuccess("Orden #" + idOrden + " ahora está en estado: " + actualizada.estadoOrden());
    }

    private void finalizar() throws PersistenceException, OrderNotFoundException, InvalidOrderStateException {
        Integer idOrden = Integer.valueOf(DialogHelper.input("Id de la orden a finalizar:"));
        BigDecimal costoFinal = new BigDecimal(DialogHelper.input("Costo final de mano de obra:", "0.00"));
        OrdenServicio finalizada = ordenServicioController.finalizar(idOrden, costoFinal);
        BigDecimal total = ordenServicioController.calcularCostoTotal(finalizada);
        DialogHelper.showSuccess("Orden #" + idOrden + " finalizada. Costo total: " + total);
    }

    private void historialPorVehiculo() throws PersistenceException {
        Integer idVehiculo = Integer.valueOf(DialogHelper.input("Id del vehículo:"));
        List<OrdenServicio> ordenes = ordenServicioController.historialPorVehiculo(idVehiculo);

        String[] headers = {"Id orden", "Fecha ingreso", "Estado", "Mecánico", "Costo total"};
        List<String[]> filas = new ArrayList<>();
        for (OrdenServicio o : ordenes) {
            filas.add(new String[]{String.valueOf(o.idOrden()), String.valueOf(o.fechaIngreso()),
                o.estadoOrden().name(), o.mecanicoResponsable(),
                ordenServicioController.calcularCostoTotal(o).toString()});
        }
        DialogHelper.showInfo(DialogHelper.formatTable(headers, filas));
    }
}
