package com.prueba_desenpeno_java.view;

import javax.swing.JOptionPane;
import java.util.List;

public class DialogHelper {

    private DialogHelper() {
    }

    public static void showSuccess(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(String mensaje) {
        return JOptionPane.showConfirmDialog(null, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static String input(String mensaje) {
        return JOptionPane.showInputDialog(null, mensaje);
    }

    public static String input(String mensaje, String valorPorDefecto) {
        return (String) JOptionPane.showInputDialog(null, mensaje, "Entrada", JOptionPane.PLAIN_MESSAGE, null, null, valorPorDefecto);
    }

    public static int menu(String titulo, String[] opciones) {
        return JOptionPane.showOptionDialog(null, "Selecciona una opción:", titulo,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
    }

    /**
     * Tabla de texto alineada para mostrar listados dentro de JOptionPane.
     */
    public static String formatTable(String[] headers, List<String[]> filas) {
        int[] anchos = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            anchos[i] = headers[i].length();
        }
        for (String[] fila : filas) {
            for (int i = 0; i < fila.length; i++) {
                anchos[i] = Math.max(anchos[i], fila[i] == null ? 0 : fila[i].length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(construirFila(headers, anchos)).append(construirSeparador(anchos));
        for (String[] fila : filas) {
            sb.append(construirFila(fila, anchos));
        }
        if (filas.isEmpty()) {
            sb.append("(sin registros)\n");
        }
        return sb.toString();
    }

    private static String construirFila(String[] valores, int[] anchos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            sb.append(String.format("%-" + (anchos[i] + 2) + "s", valores[i] == null ? "" : valores[i]));
        }
        return sb.append("\n").toString();
    }

    private static String construirSeparador(int[] anchos) {
        int total = 0;
        for (int a : anchos) {
            total += a + 2;
        }
        return "-".repeat(total) + "\n";
    }

    public static String etiquetaEstado(boolean activo) {
        return activo ? "[ACTIVO]" : "[INACTIVO]";
    }
}
