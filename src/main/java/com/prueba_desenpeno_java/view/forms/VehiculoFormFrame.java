package com.prueba_desenpeno_java.view.forms;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.DuplicateVehiclePlacaException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Vehiculo;
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

public class VehiculoFormFrame extends JFrame {

    private final MainFrame mainFrame;
    private final Runnable onGuardado;

    private final JTextField txtPlaca = new JTextField(16);
    private final JTextField txtMarca = new JTextField(16);
    private final JTextField txtModelo = new JTextField(16);
    private final JTextField txtAnio = new JTextField(16);
    private final JTextField txtColor = new JTextField(16);
    private final JTextField txtIdCliente = new JTextField(16);
    private final JLabel lblStatus = new JLabel(" ");

    public VehiculoFormFrame(MainFrame mainFrame, Runnable onGuardado) {
        super("Registrar vehículo");
        this.mainFrame = mainFrame;
        this.onGuardado = onGuardado;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(380, 340);
        setLocationRelativeTo(mainFrame);

        construirFormulario();
    }

    private void construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int fila = 0;
        fila = agregarCampo(panel, c, fila, "Placa:", txtPlaca);
        fila = agregarCampo(panel, c, fila, "Marca:", txtMarca);
        fila = agregarCampo(panel, c, fila, "Modelo:", txtModelo);
        fila = agregarCampo(panel, c, fila, "Año:", txtAnio);
        fila = agregarCampo(panel, c, fila, "Color:", txtColor);
        fila = agregarCampo(panel, c, fila, "Id del cliente dueño:", txtIdCliente);

        JButton btnGuardar = new JButton("Registrar");
        btnGuardar.addActionListener(e -> guardar());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel();
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        c.gridx = 0;
        c.gridy = fila;
        c.gridwidth = 2;
        panel.add(botones, c);

        fila++;
        c.gridy = fila;
        panel.add(lblStatus, c);

        setContentPane(panel);
    }

    private int agregarCampo(JPanel panel, GridBagConstraints c, int fila, String etiqueta, JTextField campo) {
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        panel.add(campo, c);
        return fila + 1;
    }

    private void guardar() {
        String placa = txtPlaca.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String color = txtColor.getText().trim();

        if (placa.isEmpty()) {
            Mensajes.error(lblStatus, "La placa es obligatoria.");
            return;
        }

        int anio;
        Integer idCliente;
        try {
            anio = Integer.parseInt(txtAnio.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "El año debe ser un número entero.");
            return;
        }
        try {
            idCliente = Integer.valueOf(txtIdCliente.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "El id del cliente debe ser un número entero.");
            return;
        }

        try {
            Vehiculo creado = mainFrame.getVehiculoController().registrar(placa, marca, modelo, anio, color, idCliente);
            Mensajes.exito(lblStatus, "Vehículo registrado con id: " + creado.id());
            onGuardado.run();
            dispose();
        } catch (DuplicateVehiclePlacaException | ClienteNotFoundException e) {
            Mensajes.error(lblStatus, e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        } catch (RuntimeException e) {
            Mensajes.error(lblStatus, e.getMessage());
        }
    }
}
