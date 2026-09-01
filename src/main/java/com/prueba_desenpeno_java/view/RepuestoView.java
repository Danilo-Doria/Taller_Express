package com.prueba_desenpeno_java.view;

import com.prueba_desenpeno_java.controller.RepuestoController;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Repuesto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RepuestoView {

    private final RepuestoController repuestoController;

    public RepuestoView(RepuestoController repuestoController) {
        this.repuestoController = repuestoController;
    }

    public void mostrarMenu() {
        String[] opciones = {"Registrar repuesto", "Editar repuesto", "Activar/Desactivar", "Listar todos",
            "Filtrar por categoría", "Filtrar por proveedor", "Volver"};
        boolean salir = false;
        while (!salir) {
            int opcion = DialogHelper.menu("Gestión de Repuestos", opciones);
            try {
                switch (opcion) {
                    case 0 ->
                        registrar();
                    case 1 ->
                        editar();
                    case 2 ->
                        cambiarEstado();
                    case 3 ->
                        listar(repuestoController.listar());
                    case 4 ->
                        filtrarPorCategoria();
                    case 5 ->
                        filtrarPorProveedor();
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
        String codigo = DialogHelper.input("Código de referencia:");
        if (codigo == null) {
            return;
        }
        String nombre = DialogHelper.input("Nombre:");
        String categoria = DialogHelper.input("Categoría:");
        String proveedor = DialogHelper.input("Proveedor:");
        int stockTotal = Integer.parseInt(DialogHelper.input("Stock total:", "0"));
        int stockDisponible = Integer.parseInt(DialogHelper.input("Stock disponible:", "0"));
        BigDecimal precio = new BigDecimal(DialogHelper.input("Precio unitario:", "0.00"));

        Repuesto creado = repuestoController.registrar(codigo, nombre, categoria, proveedor,
                stockTotal, stockDisponible, precio);
        DialogHelper.showSuccess("Repuesto registrado: " + creado.codigoReferencia());
    }

    private void editar() throws PersistenceException {
        String codigo = DialogHelper.input("Código del repuesto a editar:");
        if (codigo == null) {
            return;
        }
        Repuesto actual = repuestoController.buscarPorCodigo(codigo);

        String nombre = DialogHelper.input("Nombre:", actual.nombre());
        String categoria = DialogHelper.input("Categoría:", actual.categoria());
        String proveedor = DialogHelper.input("Proveedor:", actual.proveedor());
        int stockTotal = Integer.parseInt(DialogHelper.input("Stock total:", String.valueOf(actual.stockTotal())));
        int stockDisponible = Integer.parseInt(DialogHelper.input("Stock disponible:", String.valueOf(actual.stockDisponible())));
        BigDecimal precio = new BigDecimal(DialogHelper.input("Precio unitario:", actual.precioUnitario().toString()));

        repuestoController.actualizar(codigo, nombre, categoria, proveedor, stockTotal, stockDisponible, precio);
        DialogHelper.showSuccess("Repuesto actualizado: " + codigo);
    }

    private void cambiarEstado() throws PersistenceException {
        String codigo = DialogHelper.input("Código del repuesto:");
        if (codigo == null) {
            return;
        }
        Repuesto actual = repuestoController.buscarPorCodigo(codigo);
        boolean nuevoEstado = !actual.isActivo();
        repuestoController.cambiarEstado(codigo, nuevoEstado);
        DialogHelper.showSuccess("Repuesto " + codigo + " ahora está " + DialogHelper.etiquetaEstado(nuevoEstado));
    }

    private void filtrarPorCategoria() throws PersistenceException {
        String categoria = DialogHelper.input("Categoría a filtrar:");
        if (categoria == null) {
            return;
        }
        listar(repuestoController.filtrarPorCategoria(categoria));
    }

    private void filtrarPorProveedor() throws PersistenceException {
        String proveedor = DialogHelper.input("Proveedor a filtrar:");
        if (proveedor == null) {
            return;
        }
        listar(repuestoController.filtrarPorProveedor(proveedor));
    }

    private void listar(List<Repuesto> repuestos) {
        String[] headers = {"Código", "Nombre", "Categoría", "Proveedor", "Stock", "Precio", "Estado"};
        List<String[]> filas = new ArrayList<>();
        for (Repuesto r : repuestos) {
            filas.add(new String[]{r.codigoReferencia(), r.nombre(), r.categoria(), r.proveedor(),
                r.stockDisponible() + "/" + r.stockTotal(), r.precioUnitario().toString(),
                DialogHelper.etiquetaEstado(r.isActivo())});
        }
        DialogHelper.showInfo(DialogHelper.formatTable(headers, filas));
    }
}
