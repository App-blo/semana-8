package vista;

import controlador.ControladorSpeedFast;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana principal del sistema SpeedFast, desde aquí se abren las ventanas
 * de gestión de repartidores, pedidos y entregas
 */
public class VentanaPrincipal extends JFrame {

    private ControladorSpeedFast controlador; /* Controlador compartido por todas las ventanas */

    /**
     * Constructor de la clase VentanaPrincipal
     */
    public VentanaPrincipal() {
        controlador = new ControladorSpeedFast(); /* Controlador que conecta las ventanas con la base de datos */

        setTitle("SpeedFast - Gestión de Entregas");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton btnRepartidores = new JButton("Gestión de Repartidores");
        JButton btnPedidos = new JButton("Gestión de Pedidos");
        JButton btnEntregas = new JButton("Gestión de Entregas");

        btnRepartidores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VentanaRepartidores(controlador);
            }
        });

        btnPedidos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VentanaPedidos(controlador);
            }
        });

        btnEntregas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VentanaEntregas(controlador);
            }
        });

        panelBotones.add(btnRepartidores);
        panelBotones.add(btnPedidos);
        panelBotones.add(btnEntregas);

        add(new JLabel("Bienvenido a SpeedFast", SwingConstants.CENTER), BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);

        setVisible(true);
    }
}
