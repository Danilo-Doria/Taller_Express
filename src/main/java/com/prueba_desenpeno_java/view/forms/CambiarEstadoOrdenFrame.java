package com.prueba_desenpeno_java.view.forms;

import com.prueba_desenpeno_java.exception.InvalidOrderStateException;
import com.prueba_desenpeno_java.exception.OrderNotFoundException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.EstadoOrden;
import com.prueba_desenpeno_java.model.OrdenServicio;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.util.Mensajes;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class CambiarEstadoOrdenFrame extends JFrame {

    private static final String[] ESTADOS = {"DIAGNOSTICO", "EN_REPARACION", "LISTO", "ENTREGADO"};

    private final MainFrame mainFrame;
    private final Runnable onGuardado;

    private final JTextField txtIdOrden = new JTextField(10);
    private final JComboBox<String> comboEstado = new JComboBox<>(ESTADOS);
    private final JLabel lblStatus = new JLabel(" ");

    public CambiarEstadoOrdenFrame(MainFrame mainFrame, Runnable onGuardado) {
        super("Cambiar estado de orden");
        this.mainFrame = mainFrame;
        this.onGuardado = onGuardado;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(360, 220);
        setLocationRelativeTo(mainFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Id de la orden:"), c);
        c.gridx = 1;
        panel.add(txtIdOrden, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Nuevo estado:"), c);
        c.gridx = 1;
        panel.add(comboEstado, c);

        JButton btnGuardar = new JButton("Cambiar estado");
        btnGuardar.addActionListener(e -> guardar());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel();
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        panel.add(botones, c);

        c.gridy = 3;
        panel.add(lblStatus, c);

        setContentPane(panel);
    }

    private void guardar() {
        Integer idOrden;
        try {
            idOrden = Integer.valueOf(txtIdOrden.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "El id de la orden debe ser un número entero.");
            return;
        }
        EstadoOrden nuevoEstado = EstadoOrden.valueOf((String) comboEstado.getSelectedItem());

        try {
            OrdenServicio actualizada = mainFrame.getOrdenServicioController().cambiarEstado(idOrden, nuevoEstado);
            Mensajes.exito(lblStatus, "Orden #" + idOrden + " ahora está en estado: " + actualizada.estadoOrden());
            onGuardado.run();
            dispose();
        } catch (OrderNotFoundException | InvalidOrderStateException e) {
            Mensajes.error(lblStatus, e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }
}
