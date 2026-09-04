package com.prueba_desenpeno_java.view.util;

import javax.swing.JLabel;
import java.awt.Color;

public final class Mensajes {

    private static final Color COLOR_ERROR = new Color(0xB0, 0x00, 0x20);
    private static final Color COLOR_EXITO = new Color(0x1B, 0x5E, 0x20);
    private static final Color COLOR_INFO = new Color(0x33, 0x33, 0x33);

    private Mensajes() {
    }

    public static void error(JLabel lbl, String mensaje) {
        lbl.setForeground(COLOR_ERROR);
        lbl.setText(mensaje);
    }

    public static void exito(JLabel lbl, String mensaje) {
        lbl.setForeground(COLOR_EXITO);
        lbl.setText(mensaje);
    }

    public static void info(JLabel lbl, String mensaje) {
        lbl.setForeground(COLOR_INFO);
        lbl.setText(mensaje);
    }

    public static void limpiar(JLabel lbl) {
        lbl.setText(" ");
    }
}
