package com.prueba_desenpeno_java.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:postgresql://localhost:5434/taller_express";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres_123";
    
    // Private Constructor to avoid external instantiation
    private ConexionBD() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}