package com.prueba_desenpeno_java.view.forms;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.InsufficientStockException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.RepuestoNotFoundException;
import com.prueba_desenpeno_java.exception.VehiculoNotFoundException;
import com.prueba_desenpeno_java.model.OrdenServicio;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.util.Mensajes;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrdenServicioFormFrame extends JFrame {

    private final MainFrame mainFrame;
    private final Runnable onGuardado;

    private final JTextField txtIdCliente = new JTextField(10);
    private final JTextField txtIdVehiculo = new JTextField(10);
    private final JTextField txtMecanico = new JTextField(20);
    private final JTextArea txtDescripcion = new JTextArea(3, 20);
    private final JTextArea txtDiagnostico = new JTextArea(2, 20);
    private final JTextField txtCostoManoObra = new JTextField(10);
    private final JLabel lblStatus = new JLabel(" ");

    private final JTextField txtCodigoRepuesto = new JTextField(10);
    private final JTextField txtCantidadRepuesto = new JTextField(4);
    private final DefaultTableModel modeloRepuestos = new DefaultTableModel(new Object[]{"Código", "Cantidad"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaRepuestos = new JTable(modeloRepuestos);

    public OrdenServicioFormFrame(MainFrame mainFrame, Runnable onGuardado) {
        super("Registrar orden de servicio");
        this.mainFrame = mainFrame;
        this.onGuardado = onGuardado;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 600);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout(8, 8));

        add(construirFormularioDatos(), BorderLayout.NORTH);
        add(construirSeccionRepuestos(), BorderLayout.CENTER);
        add(construirBotones(), BorderLayout.SOUTH);
    }

    private JPanel construirFormularioDatos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Datos de la orden"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int fila = 0;
        fila = agregarCampo(panel, c, fila, "Id del cliente:", txtIdCliente);
        fila = agregarCampo(panel, c, fila, "Id del vehículo:", txtIdVehiculo);
        fila = agregarCampo(panel, c, fila, "Mecánico responsable:", txtMecanico);

        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel("Descripción del problema:"), c);
        c.gridx = 1;
        panel.add(new JScrollPane(txtDescripcion), c);
        fila++;

        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel("Diagnóstico (opcional):"), c);
        c.gridx = 1;
        panel.add(new JScrollPane(txtDiagnostico), c);
        fila++;

        agregarCampo(panel, c, fila, "Costo de mano de obra:", txtCostoManoObra);

        return panel;
    }

    private JPanel construirSeccionRepuestos() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new TitledBorder("Repuestos utilizados"));

        JPanel agregar = new JPanel();
        agregar.add(new JLabel("Código:"));
        agregar.add(txtCodigoRepuesto);
        agregar.add(new JLabel("Cantidad:"));
        agregar.add(txtCantidadRepuesto);
        JButton btnAgregar = new JButton("Agregar repuesto");
        btnAgregar.addActionListener(e -> agregarRepuesto());
        agregar.add(btnAgregar);
        JButton btnQuitar = new JButton("Quitar seleccionado");
        btnQuitar.addActionListener(e -> quitarRepuestoSeleccionado());
        agregar.add(btnQuitar);

        panel.add(agregar, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaRepuestos), BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirBotones() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel botones = new JPanel();
        JButton btnGuardar = new JButton("Registrar orden");
        btnGuardar.addActionListener(e -> guardar());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        panel.add(botones, BorderLayout.NORTH);
        panel.add(lblStatus, BorderLayout.SOUTH);
        return panel;
    }

    private int agregarCampo(JPanel panel, GridBagConstraints c, int fila, String etiqueta, JTextField campo) {
        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        panel.add(campo, c);
        return fila + 1;
    }

    private void agregarRepuesto() {
        String codigo = txtCodigoRepuesto.getText().trim();
        if (codigo.isEmpty()) {
            Mensajes.error(lblStatus, "Escribe el código del repuesto a agregar.");
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidadRepuesto.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "La cantidad debe ser un número entero.");
            return;
        }
        // Si el código ya estaba en la lista, se reemplaza la cantidad (igual que Map.put en la versión original)
        for (int i = 0; i < modeloRepuestos.getRowCount(); i++) {
            if (modeloRepuestos.getValueAt(i, 0).equals(codigo)) {
                modeloRepuestos.setValueAt(cantidad, i, 1);
                Mensajes.limpiar(lblStatus);
                return;
            }
        }
        modeloRepuestos.addRow(new Object[]{codigo, cantidad});
        txtCodigoRepuesto.setText("");
        txtCantidadRepuesto.setText("");
        Mensajes.limpiar(lblStatus);
    }

    private void quitarRepuestoSeleccionado() {
        int fila = tablaRepuestos.getSelectedRow();
        if (fila < 0) {
            Mensajes.error(lblStatus, "Selecciona primero un repuesto de la lista.");
            return;
        }
        modeloRepuestos.removeRow(fila);
    }

    private void guardar() {
        Integer idCliente;
        Integer idVehiculo;
        try {
            idCliente = Integer.valueOf(txtIdCliente.getText().trim());
            idVehiculo = Integer.valueOf(txtIdVehiculo.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "Id de cliente e id de vehículo deben ser números enteros.");
            return;
        }
        BigDecimal costoManoObra;
        try {
            costoManoObra = new BigDecimal(txtCostoManoObra.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "El costo de mano de obra debe ser un número (ej: 50000).");
            return;
        }

        // Reconstruye el Map<String,Integer> preservando el orden en que se agregaron
        // los repuestos, igual que el LinkedHashMap de la versión original.
        Map<String, Integer> repuestos = new LinkedHashMap<>();
        for (int i = 0; i < modeloRepuestos.getRowCount(); i++) {
            String codigo = (String) modeloRepuestos.getValueAt(i, 0);
            int cantidad = (int) modeloRepuestos.getValueAt(i, 1);
            repuestos.put(codigo, cantidad);
        }

        try {
            OrdenServicio orden = mainFrame.getOrdenServicioController().registrar(
                    idCliente, idVehiculo, txtMecanico.getText().trim(), txtDescripcion.getText().trim(),
                    txtDiagnostico.getText().trim(), repuestos, costoManoObra);
            BigDecimal total = mainFrame.getOrdenServicioController().calcularCostoTotal(orden);
            Mensajes.exito(lblStatus, "Orden #" + orden.idOrden() + " registrada. Costo total: " + total);
            onGuardado.run();
            dispose();
        } catch (ClienteNotFoundException | VehiculoNotFoundException | RepuestoNotFoundException
                | InsufficientStockException e) {
            Mensajes.error(lblStatus, e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        } catch (RuntimeException e) {
            Mensajes.error(lblStatus, e.getMessage());
        }
    }
}
