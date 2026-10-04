package vista;

import controlador.ControladorSpeedFast;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para registrar, editar, eliminar y listar entregas. Los combos de
 * pedido y repartidor se cargan desde la base de datos y muestran "id - texto"
 * pero internamente guardan el objeto completo, así se conserva el id
 */
public class VentanaEntregas extends JFrame {

    private ControladorSpeedFast controlador;   /* Controlador compartido con las demás ventanas */
    private List<Entrega> entregas;             /* Entregas que se ven en la tabla, en el mismo orden */
    private ModeloTablaSoloLectura modeloTabla; /* Modelo de datos de la tabla */
    private JTable tabla;

    private JComboBox<Object> comboPedido;      /* Selector del pedido de la entrega */
    private JComboBox<Object> comboRepartidor;  /* Selector del repartidor de la entrega */
    private JTextField txtFecha;                /* Fecha con formato AAAA-MM-DD */
    private JTextField txtHora;                 /* Hora con formato HH:MM o HH:MM:SS */
    private JComboBox<Object> filtroPedido;     /* Filtro de la tabla por pedido */
    private JComboBox<Object> filtroRepartidor; /* Filtro de la tabla por repartidor */

    private boolean cargando = false; /* Evita que los filtros recarguen la tabla mientras se rellenan los combos */

    /**
     * Constructor con parametros de la clase VentanaEntregas
     */
    public VentanaEntregas(ControladorSpeedFast controlador) {
        this.controlador = controlador; /* Definir controlador compartido */
        this.entregas = new ArrayList<>();

        setTitle("Gestión de Entregas - SpeedFast");
        setSize(750, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        /* Formulario */
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        comboPedido = new JComboBox<>();
        comboRepartidor = new JComboBox<>();
        txtFecha = new JTextField();
        txtHora = new JTextField();

        panelFormulario.add(new JLabel("Pedido:"));
        panelFormulario.add(comboPedido);
        panelFormulario.add(new JLabel("Repartidor:"));
        panelFormulario.add(comboRepartidor);
        panelFormulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        panelFormulario.add(txtFecha);
        panelFormulario.add(new JLabel("Hora (HH:MM o HH:MM:SS):"));
        panelFormulario.add(txtHora);

        /* Filtros de la tabla */
        filtroPedido = new JComboBox<>();
        filtroRepartidor = new JComboBox<>();

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.add(new JLabel("Filtrar por pedido:"));
        panelFiltros.add(filtroPedido);
        panelFiltros.add(new JLabel("y por repartidor:"));
        panelFiltros.add(filtroRepartidor);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelFormulario, BorderLayout.NORTH);
        panelNorte.add(panelFiltros, BorderLayout.SOUTH);

        /* Tabla */
        modeloTabla = new ModeloTablaSoloLectura(new String[] { "ID", "Pedido", "Repartidor", "Fecha", "Hora" });
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                    Entrega entrega = entregas.get(tabla.getSelectedRow());
                    seleccionarPorId(comboPedido, entrega.getIdPedido());
                    seleccionarPorId(comboRepartidor, entrega.getIdRepartidor());
                    txtFecha.setText(entrega.getFecha().toString());
                    txtHora.setText(entrega.getHora().toString());
                }
            }
        });

        ActionListener alFiltrar = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cargando) {
                    return;
                }
                try {
                    cargarTabla();
                } catch (SQLException ex) {
                    Mensajes.errorSQL(VentanaEntregas.this, "No se pudieron cargar las entregas", ex);
                }
            }
        };
        filtroPedido.addActionListener(alFiltrar);
        filtroRepartidor.addActionListener(alFiltrar);

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

        /* Cada vez que la ventana vuelve a estar activa se refrescan los combos y la tabla,
           así se ven los pedidos y repartidores creados, editados o eliminados en otras ventanas */
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                refrescar(false);
            }
        });

        limpiar();
        refrescar(true); /* Mostrar los datos guardados al abrir la ventana */
        setVisible(true);
    }

    /**
     * Recarga los combos y la tabla desde la base de datos
     * @param avisar true para mostrar un mensaje si falla, false para solo dejarlo en la consola
     *               (al activar la ventana no se muestran mensajes, para no entrar en un ciclo de diálogos)
     */
    private void refrescar(boolean avisar) {
        try {
            cargarCombos();
            cargarTabla();
        } catch (SQLException e) {
            if (avisar) {
                Mensajes.errorSQL(this, "No se pudieron cargar los datos", e);
            } else {
                System.out.println("No se pudieron refrescar los datos: " + e.getMessage());
            }
        }
    }

    /**
     * Llena los cuatro combos con los pedidos y repartidores de la base de datos,
     * conservando la selección que había si el registro sigue existiendo
     */
    private void cargarCombos() throws SQLException {
        List<Pedido> pedidos = controlador.listarPedidos();
        List<Repartidor> repartidores = controlador.listarRepartidores();

        cargando = true;
        try {
            llenarCombo(comboPedido, pedidos, false);
            llenarCombo(comboRepartidor, repartidores, false);
            llenarCombo(filtroPedido, pedidos, true);
            llenarCombo(filtroRepartidor, repartidores, true);
        } finally {
            cargando = false;
        }
    }

    /**
     * Reemplaza los elementos de un combo, si incluirTodos es true agrega "Todos" al comienzo
     */
    private void llenarCombo(JComboBox<Object> combo, List<?> elementos, boolean incluirTodos) {
        int idSeleccionado = obtenerId(combo.getSelectedItem());

        combo.removeAllItems();
        if (incluirTodos) {
            combo.addItem("Todos");
        }
        for (Object elemento : elementos) {
            combo.addItem(elemento);
        }

        if (idSeleccionado != 0) {
            seleccionarPorId(combo, idSeleccionado);
        }
        if (combo.getSelectedItem() == null && combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    /**
     * Devuelve el id de un Pedido o Repartidor, o 0 si el elemento es otra cosa (por ejemplo "Todos")
     */
    private int obtenerId(Object elemento) {
        if (elemento instanceof Pedido) {
            return ((Pedido) elemento).getIdPedido();
        }
        if (elemento instanceof Repartidor) {
            return ((Repartidor) elemento).getId();
        }
        return 0;
    }

    /**
     * Selecciona en el combo el elemento que tenga el id indicado
     */
    private void seleccionarPorId(JComboBox<Object> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (obtenerId(combo.getItemAt(i)) == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Vuelve a leer las entregas de la base de datos (aplicando los filtros
     * elegidos) y llena la tabla
     */
    private void cargarTabla() throws SQLException {
        /* Si el filtro está en "Todos" el id es 0 y no se filtra */
        int idPedido = obtenerId(filtroPedido.getSelectedItem());
        int idRepartidor = obtenerId(filtroRepartidor.getSelectedItem());

        entregas = controlador.listarEntregas(
                idPedido == 0 ? null : idPedido,
                idRepartidor == 0 ? null : idRepartidor);

        modeloTabla.setRowCount(0); /* Limpiar filas antes de recargar */

        for (Entrega entrega : entregas) {
            modeloTabla.addRow(new Object[] {
                    entrega.getId(),
                    entrega.getIdPedido() + " - " + entrega.getDireccionPedido(),
                    entrega.getIdRepartidor() + " - " + entrega.getNombreRepartidor(),
                    entrega.getFecha(),
                    entrega.getHora()
            });
        }
    }

    /**
     * Valida el formulario y arma la entrega, devuelve null si algún dato no es válido
     */
    private Entrega leerFormulario() {
        Object pedido = comboPedido.getSelectedItem();
        Object repartidor = comboRepartidor.getSelectedItem();

        if (!(pedido instanceof Pedido) || !(repartidor instanceof Repartidor)) {
            Mensajes.advertencia(this, "Para registrar una entrega debe existir al menos un pedido y un repartidor.");
            return null;
        }

        String textoFecha = txtFecha.getText().trim();
        String textoHora = txtHora.getText().trim();

        if (textoFecha.isEmpty() || textoHora.isEmpty()) {
            Mensajes.advertencia(this, "Debes ingresar la fecha y la hora de la entrega.");
            return null;
        }

        LocalDate fecha;
        LocalTime hora;

        try {
            fecha = LocalDate.parse(textoFecha);
        } catch (DateTimeParseException e) {
            Mensajes.advertencia(this, "La fecha debe tener el formato AAAA-MM-DD, por ejemplo 2026-10-14.");
            return null;
        }

        try {
            hora = LocalTime.parse(textoHora);
        } catch (DateTimeParseException e) {
            Mensajes.advertencia(this, "La hora debe tener el formato HH:MM o HH:MM:SS, por ejemplo 15:30.");
            return null;
        }

        return new Entrega(((Pedido) pedido).getIdPedido(), ((Repartidor) repartidor).getId(), fecha, hora);
    }

    /**
     * Devuelve la entrega seleccionada en la tabla, o null si no hay ninguna
     */
    private Entrega entregaSeleccionada() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            Mensajes.advertencia(this, "Selecciona una entrega de la tabla.");
            return null;
        }

        return entregas.get(fila);
    }

    /**
     * Guarda una entrega nueva en la base de datos
     */
    private void registrar() {
        Entrega entrega = leerFormulario();
        if (entrega == null) {
            return;
        }

        try {
            controlador.registrarEntrega(entrega);
            Mensajes.exito(this, "Entrega registrada correctamente.");
            limpiar();
            refrescar(true);
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo registrar la entrega", e);
        }
    }

    /**
     * Actualiza la entrega seleccionada con lo que hay en el formulario
     */
    private void editar() {
        Entrega seleccionada = entregaSeleccionada();
        if (seleccionada == null) {
            return;
        }

        Entrega entrega = leerFormulario();
        if (entrega == null) {
            return;
        }
        entrega.setId(seleccionada.getId()); /* Mantener el id de la entrega que se edita */

        try {
            controlador.actualizarEntrega(entrega);
            Mensajes.exito(this, "Entrega actualizada correctamente.");
            limpiar();
            refrescar(true);
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo actualizar la entrega", e);
        }
    }

    /**
     * Elimina la entrega seleccionada, previa confirmación
     */
    private void eliminar() {
        Entrega entrega = entregaSeleccionada();
        if (entrega == null) {
            return;
        }

        if (!Mensajes.confirmar(this, "¿Eliminar la entrega #" + entrega.getId() + "?")) {
            return;
        }

        try {
            controlador.eliminarEntrega(entrega.getId());
            Mensajes.exito(this, "Entrega eliminada correctamente.");
            limpiar();
            refrescar(true);
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo eliminar la entrega", e);
        }
    }

    /**
     * Deja el formulario con la fecha y hora actuales y quita la selección de la tabla
     */
    private void limpiar() {
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        tabla.clearSelection();
    }
}
