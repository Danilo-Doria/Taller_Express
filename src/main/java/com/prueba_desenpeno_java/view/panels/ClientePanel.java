package com.prueba_desenpeno_java.view.panels;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Cliente;
import com.prueba_desenpeno_java.model.EstadoEnum;
import com.prueba_desenpeno_java.model.Vehiculo;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.forms.ClienteFormFrame;
import com.prueba_desenpeno_java.view.forms.HistorialVehiculosFrame;
import com.prueba_desenpeno_java.view.util.Mensajes;
import com.prueba_desenpeno_java.view.util.TableUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class ClientePanel extends JPanel implements Refrescable {

    private final MainFrame mainFrame;
    private JTable tabla;
    private final JLabel lblStatus = new JLabel(" ");

    private static final String[] HEADERS = {"Id", "Documento", "Nombre", "Email", "Teléfono", "Estado"};

    public ClientePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> new ClienteFormFrame(mainFrame, null, this::refrescar).setVisible(true));
        toolbar.add(btnRegistrar);

        JButton btnEditar = new JButton("Editar seleccionado");
        btnEditar.addActionListener(e -> editarSeleccionado());
        toolbar.add(btnEditar);

        JButton btnHistorial = new JButton("Ver vehículos del seleccionado");
        btnHistorial.addActionListener(e -> verVehiculosSeleccionado());
        toolbar.add(btnHistorial);

        JButton btnVolver = new JButton("Volver al menú");
        btnVolver.addActionListener(e -> mainFrame.mostrarPanel(MainFrame.CARD_MENU));
        toolbar.add(btnVolver);

        add(toolbar, BorderLayout.NORTH);

        tabla = TableUtil.buildTable(HEADERS, List.of());
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);
    }

    private Integer obtenerIdSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.error(lblStatus, "Selecciona primero un cliente de la tabla.");
            return null;
        }
        return Integer.valueOf((String) tabla.getValueAt(fila, 0));
    }

    private void editarSeleccionado() {
        Integer id = obtenerIdSeleccionado();
        if (id == null) {
            return;
        }
        try {
            Cliente actual = mainFrame.getClienteController().buscarPorId(id);
            new ClienteFormFrame(mainFrame, actual, this::refrescar).setVisible(true);
        } catch (ClienteNotFoundException e) {
            Mensajes.error(lblStatus, e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }

    private void verVehiculosSeleccionado() {
        Integer id = obtenerIdSeleccionado();
        if (id == null) {
            return;
        }
        try {
            List<Vehiculo> vehiculos = mainFrame.getVehiculoController().historialPorCliente(id);
            new HistorialVehiculosFrame(mainFrame, id, vehiculos).setVisible(true);
            Mensajes.limpiar(lblStatus);
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }

    @Override
    public void refrescar() {
        try {
            List<Cliente> clientes = mainFrame.getClienteController().listar();
            List<String[]> filas = new ArrayList<>();
            for (Cliente c : clientes) {
                filas.add(new String[]{String.valueOf(c.id()), c.documento(), c.nombreCompleto(), c.email(),
                    c.telefono(), TableUtil.etiquetaEstado(c.estado() == EstadoEnum.ACTIVO)});
            }
            TableUtil.reemplazarContenido(tabla, HEADERS, filas);
            Mensajes.limpiar(lblStatus);
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }
}
