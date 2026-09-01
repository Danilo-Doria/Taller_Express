package com.prueba_desenpeno_java.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String mensaje) {
        super(mensaje);
    }
}
