package com.prueba_desenpeno_java.view.forms;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.UnauthorizedRoleException;
import com.prueba_desenpeno_java.model.RolEnum;
import com.prueba_desenpeno_java.model.Usuario;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.util.Mensajes;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * modoAdmin = false -> registro público (usuarioController.registrarUsuario),
 *             el rol y estado los aplica el UsuarioServiceDefaultsDecorator.
 * modoAdmin = true  -> registro administrativo (usuarioController.registrarAdmin),
 *             permite elegir el rol explícitamente.
 */
public class UsuarioFormFrame extends JFrame {

    private final MainFrame mainFrame;
    private final boolean modoAdmin;
    private final Runnable onGuardado;

    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtContrasena = new JPasswordField(18);
    private final JComboBox<RolEnum> comboRol = new JComboBox<>(RolEnum.values());
    private final JLabel lblStatus = new JLabel(" ");

    public UsuarioFormFrame(MainFrame mainFrame, boolean modoAdmin, Runnable onGuardado) {
        super(modoAdmin ? "Registrar usuario como ADMIN" : "Registrar usuario (RECEPCIONISTA)");
        this.mainFrame = mainFrame;
        this.modoAdmin = modoAdmin;
        this.onGuardado = onGuardado;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(380, modoAdmin ? 260 : 220);
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
        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel("Nuevo nombre de usuario:"), c);
        c.gridx = 1;
        panel.add(txtUsuario, c);
        fila++;

        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel("Contraseña:"), c);
        c.gridx = 1;
        panel.add(txtContrasena, c);
        fila++;

        if (modoAdmin) {
            c.gridx = 0;
            c.gridy = fila;
            panel.add(new JLabel("Rol:"), c);
            c.gridx = 1;
            panel.add(comboRol, c);
            fila++;
        }

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

    private void guardar() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Mensajes.error(lblStatus, "Usuario y contraseña son obligatorios.");
            return;
        }

        try {
            Usuario creado;
            if (modoAdmin) {
                RolEnum rol = (RolEnum) comboRol.getSelectedItem();
                creado = mainFrame.getUsuarioController().registrarAdmin(usuario, contrasena, rol,
                        mainFrame.getSesion().role());
                Mensajes.exito(lblStatus, "Usuario creado: " + creado.nombreUsuario() + " (role=" + creado.role() + ")");
            } else {
                creado = mainFrame.getUsuarioController().registrarUsuario(usuario, contrasena);
                Mensajes.exito(lblStatus, "Usuario creado: " + creado.nombreUsuario()
                        + " (role=" + creado.role() + ", estado=" + creado.estado() + ")");
            }
            onGuardado.run();
            dispose();
        } catch (UnauthorizedRoleException e) {
            Mensajes.error(lblStatus, "Acceso denegado: " + e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        } catch (RuntimeException e) {
            Mensajes.error(lblStatus, e.getMessage());
        }
    }
}
