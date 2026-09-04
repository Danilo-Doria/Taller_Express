package com.prueba_desenpeno_java.view.panels;

import com.prueba_desenpeno_java.model.RolEnum;
import com.prueba_desenpeno_java.model.Usuario;
import com.prueba_desenpeno_java.view.MainFrame;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class MenuPanel extends JPanel {

    private final MainFrame mainFrame;
    private final JLabel lblBienvenida = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnUsuarios = new JButton("Usuarios");

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel contenido = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        lblBienvenida.setFont(lblBienvenida.getFont().deriveFont(Font.BOLD, 18f));
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 8, 24, 8);
        contenido.add(lblBienvenida, c);
        c.insets = new Insets(8, 8, 8, 8);

        c.gridy = 1;
        contenido.add(boton("Repuestos", () -> mainFrame.mostrarPanel(MainFrame.CARD_REPUESTOS)), c);
        c.gridy = 2;
        contenido.add(boton("Clientes", () -> mainFrame.mostrarPanel(MainFrame.CARD_CLIENTES)), c);
        c.gridy = 3;
        contenido.add(boton("Vehículos", () -> mainFrame.mostrarPanel(MainFrame.CARD_VEHICULOS)), c);
        c.gridy = 4;
        contenido.add(boton("Órdenes de Servicio", () -> mainFrame.mostrarPanel(MainFrame.CARD_ORDENES)), c);

        btnUsuarios.addActionListener(e -> mainFrame.mostrarPanel(MainFrame.CARD_USUARIOS));
        btnUsuarios.setPreferredSize(new Dimension(260, 34));
        c.gridy = 5;
        contenido.add(btnUsuarios, c);

        c.gridy = 6;
        c.insets = new Insets(24, 8, 8, 8);
        contenido.add(boton("Cerrar sesión", mainFrame::cerrarSesion), c);

        add(contenido);
    }

    private JButton boton(String texto, Runnable accion) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(260, 34));
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    public void actualizar(Usuario sesion) {
        lblBienvenida.setText("Bienvenido, " + sesion.nombreUsuario() + " (" + sesion.role() + ")");
        btnUsuarios.setVisible(sesion.role() == RolEnum.ADMIN);
    }
}
