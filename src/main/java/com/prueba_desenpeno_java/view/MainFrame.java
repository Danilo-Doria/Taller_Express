package com.prueba_desenpeno_java.view;

import com.prueba_desenpeno_java.controller.ClienteController;
import com.prueba_desenpeno_java.controller.OrdenServicioController;
import com.prueba_desenpeno_java.controller.RepuestoController;
import com.prueba_desenpeno_java.controller.UsuarioController;
import com.prueba_desenpeno_java.controller.VehiculoController;
import com.prueba_desenpeno_java.model.Usuario;
import com.prueba_desenpeno_java.view.panels.ClientePanel;
import com.prueba_desenpeno_java.view.panels.LoginPanel;
import com.prueba_desenpeno_java.view.panels.MenuPanel;
import com.prueba_desenpeno_java.view.panels.OrdenServicioPanel;
import com.prueba_desenpeno_java.view.panels.Refrescable;
import com.prueba_desenpeno_java.view.panels.RepuestoPanel;
import com.prueba_desenpeno_java.view.panels.UsuarioPanel;
import com.prueba_desenpeno_java.view.panels.VehiculoPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;

public class MainFrame extends JFrame {

    public static final String CARD_LOGIN = "LOGIN";
    public static final String CARD_MENU = "MENU";
    public static final String CARD_REPUESTOS = "REPUESTOS";
    public static final String CARD_CLIENTES = "CLIENTES";
    public static final String CARD_VEHICULOS = "VEHICULOS";
    public static final String CARD_ORDENES = "ORDENES";
    public static final String CARD_USUARIOS = "USUARIOS";

    private final UsuarioController usuarioController;
    private final RepuestoController repuestoController;
    private final ClienteController clienteController;
    private final VehiculoController vehiculoController;
    private final OrdenServicioController ordenServicioController;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private MenuPanel menuPanel;
    private RepuestoPanel repuestoPanel;
    private ClientePanel clientePanel;
    private VehiculoPanel vehiculoPanel;
    private OrdenServicioPanel ordenServicioPanel;
    private UsuarioPanel usuarioPanel;

    private Usuario sesion;

    public MainFrame(UsuarioController usuarioController, RepuestoController repuestoController,
            ClienteController clienteController, VehiculoController vehiculoController,
            OrdenServicioController ordenServicioController) {
        super("Taller Express");
        this.usuarioController = usuarioController;
        this.repuestoController = repuestoController;
        this.clienteController = clienteController;
        this.vehiculoController = vehiculoController;
        this.ordenServicioController = ordenServicioController;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setMinimumSize(new java.awt.Dimension(860, 560));
        setLocationRelativeTo(null);

        construirCards();
        setContentPane(cards);
        cardLayout.show(cards, CARD_LOGIN);
    }

    private void construirCards() {
        cards.add(new LoginPanel(this), CARD_LOGIN);

        menuPanel = new MenuPanel(this);
        cards.add(menuPanel, CARD_MENU);

        repuestoPanel = new RepuestoPanel(this);
        cards.add(repuestoPanel, CARD_REPUESTOS);

        clientePanel = new ClientePanel(this);
        cards.add(clientePanel, CARD_CLIENTES);

        vehiculoPanel = new VehiculoPanel(this);
        cards.add(vehiculoPanel, CARD_VEHICULOS);

        ordenServicioPanel = new OrdenServicioPanel(this);
        cards.add(ordenServicioPanel, CARD_ORDENES);

        usuarioPanel = new UsuarioPanel(this);
        cards.add(usuarioPanel, CARD_USUARIOS);
    }

    public void onLoginExitoso(Usuario sesion) {
        this.sesion = sesion;
        menuPanel.actualizar(sesion);
        mostrarPanel(CARD_MENU);
    }

    public void cerrarSesion() {
        this.sesion = null;
        mostrarPanel(CARD_LOGIN);
    }

    public void mostrarPanel(String nombre) {
        JPanel actual = obtenerPanelPorNombre(nombre);
        if (actual instanceof Refrescable refrescable) {
            refrescable.refrescar();
        }
        cardLayout.show(cards, nombre);
    }

    private JPanel obtenerPanelPorNombre(String nombre) {
        return switch (nombre) {
            case CARD_REPUESTOS -> repuestoPanel;
            case CARD_CLIENTES -> clientePanel;
            case CARD_VEHICULOS -> vehiculoPanel;
            case CARD_ORDENES -> ordenServicioPanel;
            case CARD_USUARIOS -> usuarioPanel;
            case CARD_MENU -> menuPanel;
            default -> null;
        };
    }

    public Usuario getSesion() {
        return sesion;
    }

    public UsuarioController getUsuarioController() {
        return usuarioController;
    }

    public RepuestoController getRepuestoController() {
        return repuestoController;
    }

    public ClienteController getClienteController() {
        return clienteController;
    }

    public VehiculoController getVehiculoController() {
        return vehiculoController;
    }

    public OrdenServicioController getOrdenServicioController() {
        return ordenServicioController;
    }

    public void iniciar() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
