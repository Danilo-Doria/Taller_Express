package com.prueba_desenpeno_java.controller;

import com.prueba_desenpeno_java.exception.*;
import com.prueba_desenpeno_java.model.Repuesto;
import com.prueba_desenpeno_java.service.RepuestoService;

import java.math.BigDecimal;
import java.util.List;

public class RepuestoController {

    private final RepuestoService repuestoService;

    public RepuestoController(RepuestoService repuestoService) {
        this.repuestoService = repuestoService;
    }

    public Repuesto registrar(String codigo, String nombre, String categoria, String proveedor,
            int stockTotal, int stockDisponible, BigDecimal precioUnitario)
            throws PersistenceException, DuplicateRepuestoException {
        Repuesto repuesto = new Repuesto(codigo, nombre, categoria, proveedor, stockTotal, stockDisponible,
                precioUnitario, true, null);
        return repuestoService.registrar(repuesto);
    }

    public Repuesto actualizar(String codigo, String nombre, String categoria, String proveedor,
            int stockTotal, int stockDisponible, BigDecimal precioUnitario)
            throws PersistenceException, RepuestoNotFoundException {
        Repuesto repuesto = new Repuesto(codigo, nombre, categoria, proveedor, stockTotal, stockDisponible,
                precioUnitario, true, null);
        return repuestoService.actualizar(repuesto);
    }

    public Repuesto cambiarEstado(String codigo, boolean activo) throws PersistenceException, RepuestoNotFoundException {
        return repuestoService.cambiarEstado(codigo, activo);
    }

    public List<Repuesto> listar() throws PersistenceException {
        return repuestoService.listar();
    }

    public List<Repuesto> filtrarPorCategoria(String categoria) throws PersistenceException {
        return repuestoService.filtrarPorCategoria(categoria);
    }

    public List<Repuesto> filtrarPorProveedor(String proveedor) throws PersistenceException {
        return repuestoService.filtrarPorProveedor(proveedor);
    }

    public Repuesto buscarPorCodigo(String codigo) throws PersistenceException, RepuestoNotFoundException {
        return repuestoService.buscarPorCodigo(codigo);
    }
}
