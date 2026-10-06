package main;

import comun.GestorRed;
import comun.GestorRedLocal;
import comun.Sesion;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import parchis.AppJugador;

public class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        SwingUtilities.invokeLater(() -> {
            Sesion sesionA = new Sesion();
            Sesion sesionB = new Sesion();
            Sesion sesionC = new Sesion();
            GestorRed red = new GestorRedLocal(sesionA);

            new AppJugador("Jugador 1", sesionA, red).iniciar();
            new AppJugador("Jugador 2", sesionB, red).iniciar();
            new AppJugador("Jugador 3", sesionC, red).iniciar();
        });
    }
}
