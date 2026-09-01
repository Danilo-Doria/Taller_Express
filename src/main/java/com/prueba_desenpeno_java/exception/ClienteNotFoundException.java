package com.prueba_desenpeno_java.exception;

public class ClienteNotFoundException extends RuntimeException{
    public ClienteNotFoundException(String mensaje) {
        super(mensaje);
    }
}
