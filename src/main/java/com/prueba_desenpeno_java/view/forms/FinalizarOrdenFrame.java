package com.prueba_desenpeno_java.view.forms;

import com.prueba_desenpeno_java.exception.InvalidOrderStateException;
import com.prueba_desenpeno_java.exception.OrderNotFoundException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.OrdenServicio;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.util.Mensajes;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;

public class FinalizarOrdenFrame extends JFrame {

    private final MainFrame mainFrame;
    private final Runnable onGuardado;

    private final JTextField txtIdOrden = new JTextField(10);
    private final JTextField txtCostoFinal = new JTextField(10);
    private final JLabel lblStatus = new JLabel(" ");

    public FinalizarOrdenFrame(MainFrame mainFrame, Runnable onGuardado) {
        super("Finalizar orden de servicio");
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
        panel.add(new JLabel("Id de la orden a finalizar:"), c);
        c.gridx = 1;
        panel.add(txtIdOrden, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Costo final de mano de obra:"), c);
        c.gridx = 1;
        panel.add(txtCostoFinal, c);

        JButton btnGuardar = new JButton("Finalizar");
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
        BigDecimal costoFinal;
        try {
            idOrden = Integer.valueOf(txtIdOrden.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "El id de la orden debe ser un número entero.");
            return;
        }
        try {
            costoFinal = new BigDecimal(txtCostoFinal.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "El costo final debe ser un número (ej: 120000).");
            return;
        }

        try {
            OrdenServicio finalizada = mainFrame.getOrdenServicioController().finalizar(idOrden, costoFinal);
            BigDecimal total = mainFrame.getOrdenServicioController().calcularCostoTotal(finalizada);
            Mensajes.exito(lblStatus, "Orden #" + idOrden + " finalizada. Costo total: " + total);
            onGuardado.run();
            dispose();
        } catch (OrderNotFoundException | InvalidOrderStateException e) {
            Mensajes.error(lblStatus, e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }
}
