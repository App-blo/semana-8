package main;

import vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

/**
 * Clase principal que inicia la aplicación SpeedFast
 */
public class Main {

    public static void main(String[] args) {
        /* Iniciar la interfaz en el Event Dispatch Thread de Swing */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VentanaPrincipal();
            }
        });
    }
}
