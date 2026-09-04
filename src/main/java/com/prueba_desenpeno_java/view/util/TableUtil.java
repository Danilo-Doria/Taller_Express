package com.prueba_desenpeno_java.view.util;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public final class TableUtil {

    private TableUtil() {
    }

    public static DefaultTableModel buildModel(String[] headers, List<String[]> filas) {
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (String[] fila : filas) {
            model.addRow(fila);
        }
        return model;
    }

    public static JTable buildTable(String[] headers, List<String[]> filas) {
        JTable table = new JTable(buildModel(headers, filas));
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

    public static void reemplazarContenido(JTable table, String[] headers, List<String[]> filas) {
        table.setModel(buildModel(headers, filas));
    }

    public static String etiquetaEstado(boolean activo) {
        return activo ? "ACTIVO" : "INACTIVO";
    }
}
