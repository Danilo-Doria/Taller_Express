package com.prueba_desenpeno_java.exception;

public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(String mensaje) {
        super(mensaje);
    }
}
