package com.prueba_desenpeno_java.view.panels;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.OrdenServicio;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.forms.CambiarEstadoOrdenFrame;
import com.prueba_desenpeno_java.view.forms.FinalizarOrdenFrame;
import com.prueba_desenpeno_java.view.forms.OrdenServicioFormFrame;
import com.prueba_desenpeno_java.view.util.Mensajes;
import com.prueba_desenpeno_java.view.util.TableUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class OrdenServicioPanel extends JPanel implements Refrescable {

    private final MainFrame mainFrame;
    private JTable tabla;
    private final JLabel lblStatus = new JLabel(" ");
    private final JTextField txtIdVehiculo = new JTextField(8);

    private static final String[] HEADERS = {"Id orden", "Fecha ingreso", "Estado", "Mecánico", "Costo total"};

    public OrdenServicioPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        JButton btnRegistrar = new JButton("Registrar orden");
        btnRegistrar.addActionListener(e -> new OrdenServicioFormFrame(mainFrame, this::refrescar).setVisible(true));
        toolbar.add(btnRegistrar);

        JButton btnCambiarEstado = new JButton("Cambiar estado");
        btnCambiarEstado.addActionListener(e -> new CambiarEstadoOrdenFrame(mainFrame, this::refrescar).setVisible(true));
        toolbar.add(btnCambiarEstado);

        JButton btnFinalizar = new JButton("Finalizar orden");
        btnFinalizar.addActionListener(e -> new FinalizarOrdenFrame(mainFrame, this::refrescar).setVisible(true));
        toolbar.add(btnFinalizar);

        toolbar.add(new JLabel("Id vehículo:"));
        toolbar.add(txtIdVehiculo);
        JButton btnHistorial = new JButton("Ver historial");
        btnHistorial.addActionListener(e -> verHistorial());
        toolbar.add(btnHistorial);

        JButton btnVolver = new JButton("Volver al menú");
        btnVolver.addActionListener(e -> mainFrame.mostrarPanel(MainFrame.CARD_MENU));
        toolbar.add(btnVolver);

        add(toolbar, BorderLayout.NORTH);

        tabla = TableUtil.buildTable(HEADERS, List.of());
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void verHistorial() {
        Integer idVehiculo;
        try {
            idVehiculo = Integer.valueOf(txtIdVehiculo.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "Escribe un id de vehículo válido (número entero).");
            return;
        }
        try {
            List<OrdenServicio> ordenes = mainFrame.getOrdenServicioController().historialPorVehiculo(idVehiculo);
            List<String[]> filas = new ArrayList<>();
            for (OrdenServicio o : ordenes) {
                filas.add(new String[]{String.valueOf(o.idOrden()), String.valueOf(o.fechaIngreso()),
                    o.estadoOrden().name(), o.mecanicoResponsable(),
                    mainFrame.getOrdenServicioController().calcularCostoTotal(o).toString()});
            }
            TableUtil.reemplazarContenido(tabla, HEADERS, filas);
            Mensajes.limpiar(lblStatus);
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }

    @Override
    public void refrescar() {
        TableUtil.reemplazarContenido(tabla, HEADERS, List.of());
        Mensajes.limpiar(lblStatus);
    }
}
