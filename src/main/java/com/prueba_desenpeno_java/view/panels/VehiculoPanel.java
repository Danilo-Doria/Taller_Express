package com.prueba_desenpeno_java.view.panels;

import com.prueba_desenpeno_java.exception.PersistenceException;
import com.prueba_desenpeno_java.exception.VehiculoNotFoundException;
import com.prueba_desenpeno_java.model.Vehiculo;
import com.prueba_desenpeno_java.view.MainFrame;
import com.prueba_desenpeno_java.view.forms.VehiculoFormFrame;
import com.prueba_desenpeno_java.view.util.Mensajes;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class VehiculoPanel extends JPanel implements Refrescable {

    private final MainFrame mainFrame;
    private final JTextField txtPlaca = new JTextField(12);
    private final JLabel lblStatus = new JLabel(" ");

    private final JLabel lblId = new JLabel("-");
    private final JLabel lblPlacaDet = new JLabel("-");
    private final JLabel lblMarca = new JLabel("-");
    private final JLabel lblModelo = new JLabel("-");
    private final JLabel lblAnio = new JLabel("-");
    private final JLabel lblColor = new JLabel("-");
    private final JLabel lblClienteId = new JLabel("-");

    public VehiculoPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        JButton btnRegistrar = new JButton("Registrar vehículo");
        btnRegistrar.addActionListener(e -> new VehiculoFormFrame(mainFrame, this::limpiarDetalle).setVisible(true));
        toolbar.add(btnRegistrar);

        toolbar.add(new JLabel("Placa:"));
        toolbar.add(txtPlaca);
        JButton btnBuscar = new JButton("Buscar por placa");
        btnBuscar.addActionListener(e -> buscarPorPlaca());
        toolbar.add(btnBuscar);

        JButton btnVolver = new JButton("Volver al menú");
        btnVolver.addActionListener(e -> mainFrame.mostrarPanel(MainFrame.CARD_MENU));
        toolbar.add(btnVolver);

        add(toolbar, BorderLayout.NORTH);
        add(construirDetalle(), BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);
    }

    private JPanel construirDetalle() {
        JPanel detalle = new JPanel(new GridBagLayout());
        detalle.setBorder(new TitledBorder("Resultado de la búsqueda"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.anchor = GridBagConstraints.WEST;

        agregarFila(detalle, c, 0, "Id:", lblId);
        agregarFila(detalle, c, 1, "Placa:", lblPlacaDet);
        agregarFila(detalle, c, 2, "Marca:", lblMarca);
        agregarFila(detalle, c, 3, "Modelo:", lblModelo);
        agregarFila(detalle, c, 4, "Año:", lblAnio);
        agregarFila(detalle, c, 5, "Color:", lblColor);
        agregarFila(detalle, c, 6, "Cliente id:", lblClienteId);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(detalle, BorderLayout.NORTH);
        JLabel ayuda = new JLabel("Para ver todos los vehículos de un cliente, usa el módulo Clientes → \"Ver vehículos del seleccionado\".");
        contenedor.add(ayuda, BorderLayout.SOUTH);
        return contenedor;
    }

    private void agregarFila(JPanel panel, GridBagConstraints c, int fila, String etiqueta, JLabel valor) {
        c.gridx = 0;
        c.gridy = fila;
        panel.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        panel.add(valor, c);
    }

    private void buscarPorPlaca() {
        String placa = txtPlaca.getText().trim();
        if (placa.isEmpty()) {
            Mensajes.error(lblStatus, "Escribe una placa para buscar.");
            return;
        }
        try {
            Vehiculo v = mainFrame.getVehiculoController().buscarPorPlaca(placa);
            lblId.setText(String.valueOf(v.id()));
            lblPlacaDet.setText(v.placa());
            lblMarca.setText(v.marca());
            lblModelo.setText(v.modelo());
            lblAnio.setText(String.valueOf(v.anio()));
            lblColor.setText(v.color());
            lblClienteId.setText(String.valueOf(v.idCliente()));
            Mensajes.limpiar(lblStatus);
        } catch (VehiculoNotFoundException e) {
            Mensajes.error(lblStatus, e.getMessage());
            limpiarDetalle();
        } catch (PersistenceException e) {
            Mensajes.error(lblStatus, "Error de persistencia: " + e.getMessage());
        }
    }

    private void limpiarDetalle() {
        lblId.setText("-");
        lblPlacaDet.setText("-");
        lblMarca.setText("-");
        lblModelo.setText("-");
        lblAnio.setText("-");
        lblColor.setText("-");
        lblClienteId.setText("-");
    }

    @Override
    public void refrescar() {
        Mensajes.limpiar(lblStatus);
    }
}
