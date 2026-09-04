package com.prueba_desenpeno_java.view.forms;

import com.prueba_desenpeno_java.model.Vehiculo;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.util.TableUtil;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

public class HistorialVehiculosFrame extends JFrame {

    private static final String[] HEADERS = {"Id", "Placa", "Marca", "Modelo", "Año", "Color"};

    public HistorialVehiculosFrame(MainFrame mainFrame, Integer idCliente, List<Vehiculo> vehiculos) {
        super("Vehículos del cliente #" + idCliente);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 340);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout(8, 8));

        List<String[]> filas = new ArrayList<>();
        for (Vehiculo v : vehiculos) {
            filas.add(new String[]{String.valueOf(v.id()), v.placa(), v.marca(), v.modelo(),
                String.valueOf(v.anio()), v.color()});
        }

        JTable tabla = TableUtil.buildTable(HEADERS, filas);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel sur = new JPanel();
        sur.setBorder(new EmptyBorder(6, 6, 6, 6));
        sur.add(btnCerrar);
        add(sur, BorderLayout.SOUTH);
    }
}
