package com.prueba_desenpeno_java.view.forms;

import com.prueba_desenpeno_java.exception.ClienteNotFoundException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.model.Cliente;
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

public class ClienteFormFrame extends JFrame {

    private final MainFrame mainFrame;
    private final Cliente existente;
    private final Runnable onGuardado;

    private final JTextField txtDocumento = new JTextField(20);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(20);
    private final JLabel lblStatus = new JLabel(" ");

    public ClienteFormFrame(MainFrame mainFrame, Cliente existente, Runnable onGuardado) {
        super(existente == null ? "Registrar cliente" : "Editar cliente #" + existente.id());
        this.mainFrame = mainFrame;
        this.existente = existente;
        this.onGuardado = onGuardado;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(380, 300);
        setLocationRelativeTo(mainFrame);

        construirFormulario();
        if (existente != null) {
            precargar(existente);
        }
    }

    private void construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int fila = 0;
        fila = agregarCampo(panel, c, fila, "Documento:", txtDocumento);
        fila = agregarCampo(panel, c, fila, "Nombre completo:", txtNombre);
        fila = agregarCampo(panel, c, fila, "Email:", txtEmail);
        fila = agregarCampo(panel, c, fila, "Teléfono:", txtTelefono);

        JButton btnGuardar = new JButton(existente == null ? "Registrar" : "Actualizar");
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

    private void precargar(Cliente cliente) {
        txtDocumento.setText(cliente.documento());
        txtNombre.setText(cliente.nombreCompleto());
        txtEmail.setText(cliente.email());
        txtTelefono.setText(cliente.telefono());
    }

    private void guardar() {
        String documento = txtDocumento.getText().trim();
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (documento.isEmpty() || nombre.isEmpty()) {
            Mensajes.error(lblStatus, "Documento y nombre son obligatorios.");
            return;
        }

        try {
            if (existente == null) {
                Cliente creado = mainFrame.getClienteController().registrar(documento, nombre, email, telefono);
                Mensajes.exito(lblStatus, "Cliente registrado con id: " + creado.id());
            } else {
                mainFrame.getClienteController().actualizar(existente.id(), documento, nombre, email, telefono);
            }
            onGuardado.run();
            dispose();
        } catch (ClienteNotFoundException e) {
            Mensajes.error(lblStatus, e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        } catch (RuntimeException e) {
            Mensajes.error(lblStatus, e.getMessage());
        }
    }
}
