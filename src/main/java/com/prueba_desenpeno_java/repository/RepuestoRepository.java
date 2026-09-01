package com.prueba_desenpeno_java.repository;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Repuesto;
import java.util.List;

public interface RepuestoRepository extends CrudRepository<Repuesto, String> {

    List<Repuesto> buscarPorCategoria(String categoria) throws PersistenceException;

    List<Repuesto> buscarPorProveedor(String proveedor) throws PersistenceException;

    boolean actualizarStock(String codigoReferencia, int cantidadVendida) throws PersistenceException;

    boolean actualizarStock(String codigoReferencia, int cantidadVendida, java.sql.Connection conn) throws PersistenceException;
}
