package com.prueba_desenpeno_java.view.forms;

import com.prueba_desenpeno_java.exception.DuplicateRepuestoException;
import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.RepuestoNotFoundException;
import com.prueba_desenpeno_java.model.Repuesto;
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

public class RepuestoFormFrame extends JFrame {

    private final MainFrame mainFrame;
    private final Repuesto existente;
    private final Runnable onGuardado;

    private final JTextField txtCodigo = new JTextField(20);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtCategoria = new JTextField(20);
    private final JTextField txtProveedor = new JTextField(20);
    private final JTextField txtStockTotal = new JTextField(20);
    private final JTextField txtStockDisponible = new JTextField(20);
    private final JTextField txtPrecio = new JTextField(20);
    private final JLabel lblStatus = new JLabel(" ");

    public RepuestoFormFrame(MainFrame mainFrame, Repuesto existente, Runnable onGuardado) {
        super(existente == null ? "Registrar repuesto" : "Editar repuesto: " + existente.codigoReferencia());
        this.mainFrame = mainFrame;
        this.existente = existente;
        this.onGuardado = onGuardado;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 380);
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
        fila = agregarCampo(panel, c, fila, "Código de referencia:", txtCodigo);
        fila = agregarCampo(panel, c, fila, "Nombre:", txtNombre);
        fila = agregarCampo(panel, c, fila, "Categoría:", txtCategoria);
        fila = agregarCampo(panel, c, fila, "Proveedor:", txtProveedor);
        fila = agregarCampo(panel, c, fila, "Stock total:", txtStockTotal);
        fila = agregarCampo(panel, c, fila, "Stock disponible:", txtStockDisponible);
        fila = agregarCampo(panel, c, fila, "Precio unitario:", txtPrecio);

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

    private void precargar(Repuesto r) {
        txtCodigo.setText(r.codigoReferencia());
        txtCodigo.setEditable(false);
        txtNombre.setText(r.nombre());
        txtCategoria.setText(r.categoria());
        txtProveedor.setText(r.proveedor());
        txtStockTotal.setText(String.valueOf(r.stockTotal()));
        txtStockDisponible.setText(String.valueOf(r.stockDisponible()));
        txtPrecio.setText(r.precioUnitario().toString());
    }

    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String proveedor = txtProveedor.getText().trim();

        if (codigo.isEmpty()) {
            Mensajes.error(lblStatus, "El código de referencia es obligatorio.");
            return;
        }

        int stockTotal;
        int stockDisponible;
        BigDecimal precio;
        try {
            stockTotal = Integer.parseInt(txtStockTotal.getText().trim());
            stockDisponible = Integer.parseInt(txtStockDisponible.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "Stock total y disponible deben ser números enteros.");
            return;
        }
        try {
            precio = new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            Mensajes.error(lblStatus, "El precio unitario debe ser un número (ej: 25.50).");
            return;
        }

        try {
            if (existente == null) {
                mainFrame.getRepuestoController().registrar(codigo, nombre, categoria, proveedor,
                        stockTotal, stockDisponible, precio);
            } else {
                mainFrame.getRepuestoController().actualizar(codigo, nombre, categoria, proveedor,
                        stockTotal, stockDisponible, precio);
            }
            onGuardado.run();
            dispose();
        } catch (DuplicateRepuestoException | RepuestoNotFoundException e) {
            Mensajes.error(lblStatus, e.getMessage());
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        } catch (RuntimeException e) {
            Mensajes.error(lblStatus, e.getMessage());
        }
    }
}
