package com.prueba_desenpeno_java.service;

import com.prueba_desenpeno_java.exception.DuplicateRepuestoException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.RepuestoNotFoundException;
import com.prueba_desenpeno_java.model.Repuesto;
import java.util.List;

public interface RepuestoService {

    Repuesto registrar(Repuesto repuesto) throws PersistenceException, DuplicateRepuestoException;

    Repuesto actualizar(Repuesto repuesto) throws PersistenceException, RepuestoNotFoundException;

    Repuesto cambiarEstado(String codigoReferencia, boolean activo) throws PersistenceException, RepuestoNotFoundException;

    List<Repuesto> listar() throws PersistenceException;

    List<Repuesto> filtrarPorCategoria(String categoria) throws PersistenceException;

    List<Repuesto> filtrarPorProveedor(String proveedor) throws PersistenceException;

    Repuesto buscarPorCodigo(String codigoReferencia) throws PersistenceException, RepuestoNotFoundException;
}
