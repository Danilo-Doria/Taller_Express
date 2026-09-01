package com.prueba_desenpeno_java.exception;

public class PersistenceException extends Exception {

    public PersistenceException(String mensaje) {
        super(mensaje);
    }

    public PersistenceException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
