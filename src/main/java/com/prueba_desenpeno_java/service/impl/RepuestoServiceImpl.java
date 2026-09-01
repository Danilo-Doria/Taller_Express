package com.prueba_desenpeno_java.service.impl;

import com.prueba_desenpeno_java.exception.DuplicateRepuestoException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.RepuestoNotFoundException;
import com.prueba_desenpeno_java.model.Repuesto;
import com.prueba_desenpeno_java.repository.RepuestoRepository;
import com.prueba_desenpeno_java.repository.impl.RepuestoRepositoryImpl;
import com.prueba_desenpeno_java.service.RepuestoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RepuestoServiceImpl implements RepuestoService {

    private final RepuestoRepository repuestoRepository;

    public RepuestoServiceImpl() {
        this.repuestoRepository = new RepuestoRepositoryImpl();
    }

    public RepuestoServiceImpl(RepuestoRepository repuestoRepository) {
        this.repuestoRepository = repuestoRepository;
    }

    @Override
    public Repuesto registrar(Repuesto repuesto) throws PersistenceException, DuplicateRepuestoException {
        if (repuestoRepository.buscarPorId(repuesto.codigoReferencia()).isPresent()) {
            throw new DuplicateRepuestoException("Ya existe un repuesto con el código: " + repuesto.codigoReferencia());
        }
        if (repuesto.stockDisponible() < 0 || repuesto.stockTotal() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        if (repuesto.precioUnitario() == null || repuesto.precioUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }

        Repuesto nuevo = new Repuesto(repuesto.codigoReferencia(), repuesto.nombre(), repuesto.categoria(),
                repuesto.proveedor(), repuesto.stockTotal(), repuesto.stockDisponible(),
                repuesto.precioUnitario(), true, LocalDateTime.now());
        return repuestoRepository.guardar(nuevo);
    }

    @Override
    public Repuesto actualizar(Repuesto repuesto) throws PersistenceException, RepuestoNotFoundException {
        Repuesto existente = buscarPorCodigo(repuesto.codigoReferencia());
        Repuesto actualizado = new Repuesto(existente.codigoReferencia(), repuesto.nombre(), repuesto.categoria(),
                repuesto.proveedor(), repuesto.stockTotal(), repuesto.stockDisponible(),
                repuesto.precioUnitario(), existente.isActivo(), existente.createdAt());

        if (!repuestoRepository.actualizar(actualizado)) {
            throw new PersistenceException("No se pudo actualizar el repuesto: " + repuesto.codigoReferencia());
        }
        return actualizado;
    }

    @Override
    public Repuesto cambiarEstado(String codigoReferencia, boolean activo) throws PersistenceException, RepuestoNotFoundException {
        Repuesto existente = buscarPorCodigo(codigoReferencia);
        Repuesto actualizado = new Repuesto(existente.codigoReferencia(), existente.nombre(), existente.categoria(),
                existente.proveedor(), existente.stockTotal(), existente.stockDisponible(),
                existente.precioUnitario(), activo, existente.createdAt());
        repuestoRepository.actualizar(actualizado);
        return actualizado;
    }

    @Override
    public List<Repuesto> listar() throws PersistenceException {
        return repuestoRepository.listar();
    }

    @Override
    public List<Repuesto> filtrarPorCategoria(String categoria) throws PersistenceException {
        return repuestoRepository.buscarPorCategoria(categoria);
    }

    @Override
    public List<Repuesto> filtrarPorProveedor(String proveedor) throws PersistenceException {
        return repuestoRepository.buscarPorProveedor(proveedor);
    }

    @Override
    public Repuesto buscarPorCodigo(String codigoReferencia) throws PersistenceException, RepuestoNotFoundException {
        return repuestoRepository.buscarPorId(codigoReferencia)
                .orElseThrow(() -> new RepuestoNotFoundException("No existe repuesto con código: " + codigoReferencia));
    }
}
