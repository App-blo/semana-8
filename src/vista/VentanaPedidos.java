package vista;

import controlador.ControladorSpeedFast;
import modelo.EstadoPedido;
import modelo.Pedido;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para registrar, editar, eliminar y listar pedidos, con filtros
 * opcionales por tipo y por estado
 */
public class VentanaPedidos extends JFrame {

    private static final String[] TIPOS = { "COMIDA", "ENCOMIENDA", "EXPRESS" }; /* Tipos válidos de pedido */

    private ControladorSpeedFast controlador;   /* Controlador compartido con las demás ventanas */
    private List<Pedido> pedidos;               /* Pedidos que se ven en la tabla, en el mismo orden */
    private ModeloTablaSoloLectura modeloTabla; /* Modelo de datos de la tabla */
    private JTable tabla;

    private JTextField txtDireccion;               /* Campo para la dirección de entrega */
    private JComboBox<String> comboTipo;           /* Selector del tipo de pedido */
    private JComboBox<EstadoPedido> comboEstado;   /* Selector del estado del pedido */
    private JComboBox<String> filtroTipo;          /* Filtro de la tabla por tipo */
    private JComboBox<String> filtroEstado;        /* Filtro de la tabla por estado */

    /**
     * Constructor con parametros de la clase VentanaPedidos
     */
    public VentanaPedidos(ControladorSpeedFast controlador) {
        this.controlador = controlador; /* Definir controlador compartido */
        this.pedidos = new ArrayList<>();

        setTitle("Gestión de Pedidos - SpeedFast");
        setSize(650, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        /* Formulario */
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        txtDireccion = new JTextField();
        comboTipo = new JComboBox<>(TIPOS);
        comboEstado = new JComboBox<>(EstadoPedido.values());

        panelFormulario.add(new JLabel("Dirección de entrega:"));
        panelFormulario.add(txtDireccion);
        panelFormulario.add(new JLabel("Tipo de pedido:"));
        panelFormulario.add(comboTipo);
        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(comboEstado);

        /* Filtros de la tabla */
        filtroTipo = new JComboBox<>(new String[] { "TODOS", "COMIDA", "ENCOMIENDA", "EXPRESS" });
        filtroEstado = new JComboBox<>(new String[] { "TODOS", "PENDIENTE", "EN_REPARTO", "ENTREGADO" });

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.add(new JLabel("Filtrar por tipo:"));
        panelFiltros.add(filtroTipo);
        panelFiltros.add(new JLabel("y por estado:"));
        panelFiltros.add(filtroEstado);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelFormulario, BorderLayout.NORTH);
        panelNorte.add(panelFiltros, BorderLayout.SOUTH);

        /* Tabla */
        modeloTabla = new ModeloTablaSoloLectura(new String[] { "ID", "Dirección", "Tipo", "Estado" });
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                    Pedido pedido = pedidos.get(tabla.getSelectedRow());
                    txtDireccion.setText(pedido.getDireccionEntrega());
                    comboTipo.setSelectedItem(pedido.getTipoPedido());
                    comboEstado.setSelectedItem(pedido.getEstado());
                }
            }
        });

        ActionListener alFiltrar = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarTabla();
            }
        };
        filtroTipo.addActionListener(alFiltrar);
        filtroEstado.addActionListener(alFiltrar);

        /* Botones */
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrar();
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editar();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminar();
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar();
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        cargarTabla(); /* Mostrar los pedidos guardados al abrir la ventana */
        setVisible(true);
    }

    /**
     * Vuelve a leer los pedidos de la base de datos (aplicando los filtros
     * elegidos) y llena la tabla
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0); /* Limpiar filas antes de recargar */

        /* "TODOS" significa que ese filtro no se aplica */
        String tipo = (String) filtroTipo.getSelectedItem();
        String estado = (String) filtroEstado.getSelectedItem();
        if (tipo.equals("TODOS")) {
            tipo = null;
        }
        if (estado.equals("TODOS")) {
            estado = null;
        }

        try {
            pedidos = controlador.listarPedidos(tipo, estado);
        } catch (SQLException e) {
            pedidos = new ArrayList<>();
            Mensajes.errorSQL(this, "No se pudieron cargar los pedidos", e);
            return;
        }

        for (Pedido pedido : pedidos) {
            modeloTabla.addRow(new Object[] {
                    pedido.getIdPedido(),
                    pedido.getDireccionEntrega(),
                    pedido.getTipoPedido(),
                    pedido.getEstado()
            });
        }
    }

    /**
     * Valida la dirección ingresada, devuelve null si no es válida
     */
    private String leerDireccion() {
        String direccion = txtDireccion.getText().trim();

        if (direccion.isEmpty()) {
            Mensajes.advertencia(this, "Debes ingresar la dirección de entrega.");
            return null;
        }

        if (direccion.length() > 100) {
            Mensajes.advertencia(this, "La dirección no puede superar los 100 caracteres.");
            return null;
        }

        return direccion;
    }

    /**
     * Devuelve el pedido seleccionado en la tabla, o null si no hay ninguno
     */
    private Pedido pedidoSeleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            Mensajes.advertencia(this, "Selecciona un pedido de la tabla.");
            return null;
        }

        return pedidos.get(fila);
    }

    /**
     * Guarda un pedido nuevo en la base de datos
     */
    private void registrar() {
        String direccion = leerDireccion();
        if (direccion == null) {
            return;
        }

        Pedido pedido = new Pedido(direccion, (String) comboTipo.getSelectedItem(),
                (EstadoPedido) comboEstado.getSelectedItem());

        try {
            controlador.registrarPedido(pedido);
            Mensajes.exito(this, "Pedido registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo registrar el pedido", e);
        }
    }

    /**
     * Actualiza los datos del pedido seleccionado con lo que hay en el formulario
     */
    private void editar() {
        Pedido pedido = pedidoSeleccionado();
        if (pedido == null) {
            return;
        }

        String direccion = leerDireccion();
        if (direccion == null) {
            return;
        }

        pedido.setDireccionEntrega(direccion);
        pedido.setTipoPedido((String) comboTipo.getSelectedItem());
        pedido.setEstado((EstadoPedido) comboEstado.getSelectedItem());

        try {
            controlador.actualizarPedido(pedido);
            Mensajes.exito(this, "Pedido actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo actualizar el pedido", e);
        }
    }

    /**
     * Elimina el pedido seleccionado, previa confirmación
     */
    private void eliminar() {
        Pedido pedido = pedidoSeleccionado();
        if (pedido == null) {
            return;
        }

        if (!Mensajes.confirmar(this, "¿Eliminar el pedido " + pedido + "?")) {
            return;
        }

        try {
            controlador.eliminarPedido(pedido.getIdPedido());
            Mensajes.exito(this, "Pedido eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo eliminar el pedido", e);
        }
    }

    /**
     * Deja el formulario vacío y quita la selección de la tabla
     */
    private void limpiar() {
        txtDireccion.setText("");
        comboTipo.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }
}
