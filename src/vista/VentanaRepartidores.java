package vista;

import controlador.ControladorSpeedFast;
import modelo.Repartidor;
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
 * Ventana para registrar, editar, eliminar y listar repartidores
 */
public class VentanaRepartidores extends JFrame {

    private ControladorSpeedFast controlador;   /* Controlador compartido con las demás ventanas */
    private List<Repartidor> repartidores;      /* Repartidores que se ven en la tabla, en el mismo orden */
    private ModeloTablaSoloLectura modeloTabla; /* Modelo de datos de la tabla */
    private JTable tabla;
    private JTextField txtNombre;               /* Campo para el nombre del repartidor */

    /**
     * Constructor con parametros de la clase VentanaRepartidores
     */
    public VentanaRepartidores(ControladorSpeedFast controlador) {
        this.controlador = controlador; /* Definir controlador compartido */
        this.repartidores = new ArrayList<>();

        setTitle("Gestión de Repartidores - SpeedFast");
        setSize(520, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        /* Formulario */
        JPanel panelFormulario = new JPanel(new GridLayout(1, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        txtNombre = new JTextField();
        panelFormulario.add(new JLabel("Nombre del repartidor:"));
        panelFormulario.add(txtNombre);

        /* Tabla */
        modeloTabla = new ModeloTablaSoloLectura(new String[] { "ID", "Nombre" });
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                    txtNombre.setText(repartidores.get(tabla.getSelectedRow()).getNombre());
                }
            }
        });

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

        add(panelFormulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        cargarTabla(); /* Mostrar los repartidores guardados al abrir la ventana */
        setVisible(true);
    }

    /**
     * Vuelve a leer los repartidores de la base de datos y llena la tabla
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0); /* Limpiar filas antes de recargar */

        try {
            repartidores = controlador.listarRepartidores();
        } catch (SQLException e) {
            repartidores = new ArrayList<>();
            Mensajes.errorSQL(this, "No se pudieron cargar los repartidores", e);
            return;
        }

        for (Repartidor repartidor : repartidores) {
            modeloTabla.addRow(new Object[] { repartidor.getId(), repartidor.getNombre() });
        }
    }

    /**
     * Valida el nombre ingresado, devuelve null si no es válido
     */
    private String leerNombre() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            Mensajes.advertencia(this, "Debes ingresar el nombre del repartidor.");
            return null;
        }

        if (nombre.length() > 100) {
            Mensajes.advertencia(this, "El nombre no puede superar los 100 caracteres.");
            return null;
        }

        return nombre;
    }

    /**
     * Devuelve el repartidor seleccionado en la tabla, o null si no hay ninguno
     */
    private Repartidor repartidorSeleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            Mensajes.advertencia(this, "Selecciona un repartidor de la tabla.");
            return null;
        }

        return repartidores.get(fila);
    }

    /**
     * Guarda un repartidor nuevo en la base de datos
     */
    private void registrar() {
        String nombre = leerNombre();
        if (nombre == null) {
            return;
        }

        try {
            controlador.registrarRepartidor(new Repartidor(nombre));
            Mensajes.exito(this, "Repartidor registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo registrar el repartidor", e);
        }
    }

    /**
     * Actualiza el nombre del repartidor seleccionado
     */
    private void editar() {
        Repartidor repartidor = repartidorSeleccionado();
        if (repartidor == null) {
            return;
        }

        String nombre = leerNombre();
        if (nombre == null) {
            return;
        }

        repartidor.setNombre(nombre);

        try {
            controlador.actualizarRepartidor(repartidor);
            Mensajes.exito(this, "Repartidor actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo actualizar el repartidor", e);
        }
    }

    /**
     * Elimina el repartidor seleccionado, previa confirmación
     */
    private void eliminar() {
        Repartidor repartidor = repartidorSeleccionado();
        if (repartidor == null) {
            return;
        }

        if (!Mensajes.confirmar(this, "¿Eliminar al repartidor " + repartidor + "?")) {
            return;
        }

        try {
            controlador.eliminarRepartidor(repartidor.getId());
            Mensajes.exito(this, "Repartidor eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo eliminar el repartidor", e);
        }
    }

    /**
     * Deja el formulario vacío y quita la selección de la tabla
     */
    private void limpiar() {
        txtNombre.setText("");
        tabla.clearSelection();
    }
}
