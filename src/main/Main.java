package main;

import cliente.SesionCliente;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import parchis.AppJugador;
import servidor.ParchisServidor;

/**
 * Punto de entrada del programa: abre la aplicación del jugador.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        String nombre = args.length > 0 ? args[0] : "Jugador";
        SwingUtilities.invokeLater(() -> new AppJugador(nombre, new SesionCliente(new ParchisServidor())).iniciar());
    }
}
