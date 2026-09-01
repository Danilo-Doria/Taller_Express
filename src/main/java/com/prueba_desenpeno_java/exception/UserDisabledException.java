package com.prueba_desenpeno_java.exception;

public class UserDisabledException extends RuntimeException {

    public UserDisabledException(String mensaje) {
        super(mensaje);
    }
}
