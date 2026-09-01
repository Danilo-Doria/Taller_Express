package com.prueba_desenpeno_java.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String mensaje) {
        super(mensaje);
    }
}
