package com.prueba_desenpeno_java.view;

import com.prueba_desenpeno_java.controller.UsuarioController;
import com.prueba_desenpeno_java.exception.*;
import com.prueba_desenpeno_java.model.RolEnum;
import com.prueba_desenpeno_java.model.Usuario;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.util.ArrayList;
import java.util.List;

public class UsuarioView {

    private final UsuarioController usuarioController;

    public UsuarioView(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    public Usuario login() {
        while (true) {
            String usuario = DialogHelper.input("Usuario:");
            if (usuario == null) {
                return null;
            }

            JPasswordField passwordField = new JPasswordField();
            int opcion = JOptionPane.showConfirmDialog(null, passwordField, "Contraseña", JOptionPane.OK_CANCEL_OPTION);
            if (opcion != JOptionPane.OK_OPTION) {
                return null;
            }
            String contrasena = new String(passwordField.getPassword());

            try {
                Usuario logueado = usuarioController.login(usuario, contrasena);
                DialogHelper.showSuccess("Bienvenido, " + logueado.nombreUsuario() + " (" + logueado.role() + ")");
                return logueado;
            } catch (InvalidCredentialsException | UserDisabledException e) {
                DialogHelper.showError(e.getMessage());
                if (!DialogHelper.confirm("¿Intentar de nuevo?")) {
                    return null;
                }
            } catch (PersistenceException e) {
                DialogHelper.showError("Error de persistencia: " + e.getMessage());
                return null;
            }
        }
    }

    public void mostrarMenu(RolEnum rolSesion) {
        String[] opciones = {"Registrar usuario (RECEPCIONISTA)", "Registrar usuario como ADMIN",
            "Listar usuarios", "Eliminar usuario", "Volver"};
        boolean salir = false;
        while (!salir) {
            int opcion = DialogHelper.menu("Gestión de Usuarios", opciones);
            try {
                switch (opcion) {
                    case 0 ->
                        registrarPublico();
                    case 1 ->
                        registrarComoAdmin(rolSesion);
                    case 2 ->
                        listar(rolSesion);
                    case 3 ->
                        eliminar(rolSesion);
                    default ->
                        salir = true;
                }
            } catch (UnauthorizedRoleException e) {
                DialogHelper.showError("Acceso denegado: " + e.getMessage());
            } catch (PersistenceException e) {
                DialogHelper.showError("Error de persistencia: " + e.getMessage());
            } catch (RuntimeException e) {
                DialogHelper.showError(e.getMessage());
            }
        }
    }

    private void registrarPublico() throws PersistenceException {
        String usuario = DialogHelper.input("Nuevo nombre de usuario:");
        if (usuario == null) {
            return;
        }
        String contrasena = DialogHelper.input("Contraseña:");
        Usuario creado = usuarioController.registrarUsuario(usuario, contrasena);
        DialogHelper.showSuccess("Usuario creado: " + creado.nombreUsuario()
                + " (role=" + creado.role() + ", estado=" + creado.estado() + ")");
    }

    private void registrarComoAdmin(RolEnum rolSesion) throws PersistenceException, UnauthorizedRoleException {
        String usuario = DialogHelper.input("Nuevo nombre de usuario:");
        if (usuario == null) {
            return;
        }
        String contrasena = DialogHelper.input("Contraseña:");
        String[] roles = {"ADMIN", "RECEPCIONISTA"};
        int idx = DialogHelper.menu("Rol del nuevo usuario", roles);
        if (idx < 0) {
            return;
        }
        RolEnum rol = idx == 0 ? RolEnum.ADMIN : RolEnum.RECEPCIONISTA;

        Usuario creado = usuarioController.registrarAdmin(usuario, contrasena, rol, rolSesion);
        DialogHelper.showSuccess("Usuario creado: " + creado.nombreUsuario() + " (role=" + creado.role() + ")");
    }

    private void listar(RolEnum rolSesion) throws PersistenceException, UnauthorizedRoleException {
        List<Usuario> usuarios = usuarioController.listar(rolSesion);
        String[] headers = {"Id", "Usuario", "Rol", "Estado", "Creado"};
        List<String[]> filas = new ArrayList<>();
        for (Usuario u : usuarios) {
            filas.add(new String[]{String.valueOf(u.id()), u.nombreUsuario(), u.role().name(),
                u.estado().name(), String.valueOf(u.createdAt())});
        }
        DialogHelper.showInfo(DialogHelper.formatTable(headers, filas));
    }

    private void eliminar(RolEnum rolSesion) throws PersistenceException, UnauthorizedRoleException {
        Integer id = Integer.valueOf(DialogHelper.input("Id del usuario a eliminar:"));
        if (usuarioController.eliminar(id, rolSesion)) {
            DialogHelper.showSuccess("Usuario eliminado: " + id);
        } else {
            DialogHelper.showError("No se encontró el usuario: " + id);
        }
    }
}
