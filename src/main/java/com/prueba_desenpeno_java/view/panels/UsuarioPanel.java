package com.prueba_desenpeno_java.view.panels;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.UnauthorizedRoleException;
import com.prueba_desenpeno_java.model.Usuario;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.forms.UsuarioFormFrame;
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

public class UsuarioPanel extends JPanel implements Refrescable {

    private final MainFrame mainFrame;
    private JTable tabla;
    private final JLabel lblStatus = new JLabel(" ");

    private static final String[] HEADERS = {"Id", "Usuario", "Rol", "Estado", "Creado"};

    public UsuarioPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        JButton btnRegistrarPublico = new JButton("Registrar usuario (RECEPCIONISTA)");
        btnRegistrarPublico.addActionListener(e ->
                new UsuarioFormFrame(mainFrame, false, this::refrescar).setVisible(true));
        toolbar.add(btnRegistrarPublico);

        JButton btnRegistrarAdmin = new JButton("Registrar usuario como ADMIN");
        btnRegistrarAdmin.addActionListener(e ->
                new UsuarioFormFrame(mainFrame, true, this::refrescar).setVisible(true));
        toolbar.add(btnRegistrarAdmin);

        JButton btnListar = new JButton("Listar usuarios");
        btnListar.addActionListener(e -> refrescar());
        toolbar.add(btnListar);

        JButton btnEliminar = new JButton("Eliminar seleccionado");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        toolbar.add(btnEliminar);

        JButton btnVolver = new JButton("Volver al menú");
        btnVolver.addActionListener(e -> mainFrame.mostrarPanel(MainFrame.CARD_MENU));
        toolbar.add(btnVolver);

        add(toolbar, BorderLayout.NORTH);

        tabla = TableUtil.buildTable(HEADERS, List.of());
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.error(lblStatus, "Selecciona primero un usuario de la tabla.");
            return;
        }
        Integer id = Integer.valueOf((String) tabla.getValueAt(fila, 0));
        try {
            boolean eliminado = mainFrame.getUsuarioController().eliminar(id, mainFrame.getSesion().role());
            if (eliminado) {
                Mensajes.exito(lblStatus, "Usuario eliminado: " + id);
                refrescar();
            } else {
                Mensajes.error(lblStatus, "No se encontró el usuario: " + id);
            }
        } catch (UnauthorizedRoleException e) {
            Mensajes.error(lblStatus, "Acceso denegado: " + e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }

    @Override
    public void refrescar() {
        try {
            List<Usuario> usuarios = mainFrame.getUsuarioController().listar(mainFrame.getSesion().role());
            List<String[]> filas = new ArrayList<>();
            for (Usuario u : usuarios) {
                filas.add(new String[]{String.valueOf(u.id()), u.nombreUsuario(), u.role().name(),
                    u.estado().name(), String.valueOf(u.createdAt())});
            }
            TableUtil.reemplazarContenido(tabla, HEADERS, filas);
            Mensajes.limpiar(lblStatus);
        } catch (UnauthorizedRoleException e) {
            Mensajes.error(lblStatus, "Acceso denegado: " + e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }
}
