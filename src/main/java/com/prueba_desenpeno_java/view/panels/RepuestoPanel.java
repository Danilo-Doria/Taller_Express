package com.prueba_desenpeno_java.view.panels;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.RepuestoNotFoundException;
import com.prueba_desenpeno_java.model.Repuesto;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.forms.RepuestoFormFrame;
import com.prueba_desenpeno_java.view.util.Mensajes;
import com.prueba_desenpeno_java.view.util.TableUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class RepuestoPanel extends JPanel implements Refrescable {

    private final MainFrame mainFrame;
    private JTable tabla;
    private final JLabel lblStatus = new JLabel(" ");
    private final JTextField txtCategoria = new JTextField(10);
    private final JTextField txtProveedor = new JTextField(10);

    private static final String[] HEADERS = {"Código", "Nombre", "Categoría", "Proveedor", "Stock", "Precio", "Estado"};

    public RepuestoPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(construirToolbar(), BorderLayout.NORTH);

        tabla = TableUtil.buildTable(HEADERS, List.of());
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);
    }

    private JPanel construirToolbar() {
        JPanel contenedorControles = new JPanel(new java.awt.GridLayout(2, 1, 0, 0));

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> new RepuestoFormFrame(mainFrame, null, this::refrescar).setVisible(true));
        panelAcciones.add(btnRegistrar);

        JButton btnEditar = new JButton("Editar seleccionado");
        btnEditar.addActionListener(e -> editarSeleccionado());
        panelAcciones.add(btnEditar);

        JButton btnEstado = new JButton("Activar/Desactivar seleccionado");
        btnEstado.addActionListener(e -> cambiarEstadoSeleccionado());
        panelAcciones.add(btnEstado);

        JButton btnVolver = new JButton("Volver al menú");
        btnVolver.addActionListener(e -> mainFrame.mostrarPanel(MainFrame.CARD_MENU));
        panelAcciones.add(btnVolver);

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        panelFiltros.add(new JLabel("Categoría:"));
        panelFiltros.add(txtCategoria);
        JButton btnFiltrarCategoria = new JButton("Filtrar");
        btnFiltrarCategoria.addActionListener(e -> filtrarPorCategoria());
        panelFiltros.add(btnFiltrarCategoria);

        panelFiltros.add(new JLabel(" Proveedor:")); // Espacio extra visual
        panelFiltros.add(txtProveedor);
        JButton btnFiltrarProveedor = new JButton("Filtrar");
        btnFiltrarProveedor.addActionListener(e -> filtrarPorProveedor());
        panelFiltros.add(btnFiltrarProveedor);

        JButton btnVerTodos = new JButton("Ver todos");
        btnVerTodos.addActionListener(e -> refrescar());
        panelFiltros.add(btnVerTodos);

        contenedorControles.add(panelAcciones);
        contenedorControles.add(panelFiltros);

        return contenedorControles;
    }

    private String obtenerCodigoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.error(lblStatus, "Selecciona primero un repuesto de la tabla.");
            return null;
        }
        return (String) tabla.getValueAt(fila, 0);
    }

    private void editarSeleccionado() {
        String codigo = obtenerCodigoSeleccionado();
        if (codigo == null) {
            return;
        }
        try {
            Repuesto actual = mainFrame.getRepuestoController().buscarPorCodigo(codigo);
            new RepuestoFormFrame(mainFrame, actual, this::refrescar).setVisible(true);
        } catch (RepuestoNotFoundException e) {
            Mensajes.error(lblStatus, e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }

    private void cambiarEstadoSeleccionado() {
        String codigo = obtenerCodigoSeleccionado();
        if (codigo == null) {
            return;
        }
        try {
            Repuesto actual = mainFrame.getRepuestoController().buscarPorCodigo(codigo);
            mainFrame.getRepuestoController().cambiarEstado(codigo, !actual.isActivo());
            Mensajes.exito(lblStatus, "Repuesto " + codigo + " ahora está "
                    + TableUtil.etiquetaEstado(!actual.isActivo()) + ".");
            refrescar();
        } catch (RepuestoNotFoundException e) {
            Mensajes.error(lblStatus, e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }

    private void filtrarPorCategoria() {
        String categoria = txtCategoria.getText().trim();
        if (categoria.isEmpty()) {
            Mensajes.error(lblStatus, "Escribe una categoría para filtrar.");
            return;
        }
        try {
            mostrarLista(mainFrame.getRepuestoController().filtrarPorCategoria(categoria));
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }

    private void filtrarPorProveedor() {
        String proveedor = txtProveedor.getText().trim();
        if (proveedor.isEmpty()) {
            Mensajes.error(lblStatus, "Escribe un proveedor para filtrar.");
            return;
        }
        try {
            mostrarLista(mainFrame.getRepuestoController().filtrarPorProveedor(proveedor));
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }

    private void mostrarLista(List<Repuesto> repuestos) {
        List<String[]> filas = new ArrayList<>();
        for (Repuesto r : repuestos) {
            filas.add(new String[]{r.codigoReferencia(), r.nombre(), r.categoria(), r.proveedor(),
                r.stockDisponible() + "/" + r.stockTotal(), r.precioUnitario().toString(),
                TableUtil.etiquetaEstado(r.isActivo())});
        }
        TableUtil.reemplazarContenido(tabla, HEADERS, filas);
        Mensajes.limpiar(lblStatus);
    }

    @Override
    public void refrescar() {
        try {
            mostrarLista(mainFrame.getRepuestoController().listar());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }
}
