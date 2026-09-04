package com.prueba_desenpeno_java.view.panels;

import com.prueba_desenpeno_java.exception.InvalidCredentialsException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.UserDisabledException;
import com.prueba_desenpeno_java.model.Usuario;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.util.Mensajes;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtContrasena = new JPasswordField(18);
    private final JLabel lblStatus = new JLabel(" ");

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Taller Express");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 6, 20, 6);
        form.add(titulo, c);

        c.gridwidth = 1;
        c.insets = new Insets(6, 6, 6, 6);

        c.gridx = 0;
        c.gridy = 1;
        form.add(new JLabel("Usuario:"), c);
        c.gridx = 1;
        form.add(txtUsuario, c);

        c.gridx = 0;
        c.gridy = 2;
        form.add(new JLabel("Contraseña:"), c);
        c.gridx = 1;
        form.add(txtContrasena, c);

        JButton btnLogin = new JButton("Iniciar sesión");
        btnLogin.addActionListener(e -> intentarLogin());
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(16, 6, 6, 6);
        form.add(btnLogin, c);

        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 4;
        c.insets = new Insets(4, 6, 6, 6);
        form.add(lblStatus, c);

        // Enter en cualquiera de los dos campos también intenta el login
        txtUsuario.addActionListener(e -> intentarLogin());
        txtContrasena.addActionListener(e -> intentarLogin());

        add(form);
    }

    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Mensajes.error(lblStatus, "Ingresa usuario y contraseña.");
            return;
        }

        try {
            Usuario logueado = mainFrame.getUsuarioController().login(usuario, contrasena);
            Mensajes.limpiar(lblStatus);
            txtUsuario.setText("");
            txtContrasena.setText("");
            mainFrame.onLoginExitoso(logueado);
        } catch (InvalidCredentialsException | UserDisabledException e) {
            Mensajes.error(lblStatus, e.getMessage());
            txtContrasena.setText("");
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }
}
