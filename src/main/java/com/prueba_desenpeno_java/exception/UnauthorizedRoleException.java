package com.prueba_desenpeno_java.exception;

public class UnauthorizedRoleException extends RuntimeException {

    public UnauthorizedRoleException(String mensaje) {
        super(mensaje);
    }
}
