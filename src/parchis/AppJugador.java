package parchis;

import comun.GestorRed;
import comun.Sesion;
import controlador.ConfigurarPartidaController;
import controlador.IniciarPartidaController;
import controlador.RegistroController;
import controlador.SolicitarUnirseController;
import controlador.SolicitudInicioController;
import java.awt.Window;
import javax.swing.JOptionPane;
import vista.Navegador;
import vista.swing.VentanaConfigurarPartida;
import vista.swing.VentanaInicio;
import vista.swing.VentanaRegistro;
import vista.swing.VentanaSalaEspera;
import vista.swing.VentanaUnirse;

public class AppJugador implements Navegador {

    private final String nombre;
    private final Sesion sesion;
    private final GestorRed red;
    private Window actual;

    public AppJugador(String nombre, Sesion sesion, GestorRed red) {
        this.nombre = nombre;
        this.sesion = sesion;
        this.red = red;
    }

    public void iniciar() {
        mostrarInicio();
    }

    void mostrarInicio() {
        var ventana = new VentanaInicio("Parchís - " + nombre, this::mostrarConfigurar, this::mostrarUnirse);
        mostrarVentana(ventana, ventana::mostrar);
    }

    void mostrarConfigurar() {
        var vista = new VentanaConfigurarPartida();
        var cu1 = new ConfigurarPartidaController(vista, this, sesion, red);
        vista.setControlador(cu1);
        mostrarVentana(vista, vista::mostrar);
    }

    void mostrarUnirse() {
        var vista = new VentanaUnirse();
        var cu4 = new SolicitarUnirseController(vista, this, sesion, red);
        vista.setControlador(cu4);
        mostrarVentana(vista, vista::mostrar);
    }

    @Override
    public void mostrarRegistroJugador() {
        var vista = new VentanaRegistro();
        var cu2 = new RegistroController(vista, this, sesion);
        vista.setControlador(cu2);
        mostrarVentana(vista, vista::mostrar);
        cu2.consultarColoresDisponibles();
    }

    @Override
    public void mostrarSalaEspera() {
        var vista = new VentanaSalaEspera(sesion.getJugadorLocal().getNombre(), sesion.esAnfitrion());
        var cu5 = new SolicitudInicioController(vista, this, sesion);
        var cu3 = new IniciarPartidaController(vista, this, sesion, red);
        vista.setControladores(cu5, cu3);
        mostrarVentana(vista, vista::mostrar);
        vista.mostrarJugadores(sesion.getPartida().getJugadores());
    }

    @Override
    public void mostrarTablero() {
        pendiente("VentanaTablero");
    }

    private void mostrarVentana(Window nueva, Runnable mostrar) {
        Window anterior = actual;
        actual = nueva;
        mostrar.run();
        if (anterior != null) {
            anterior.dispose();
        }
    }

    private void pendiente(String pantalla) {
        JOptionPane.showMessageDialog(null, "Pantalla pendiente: " + pantalla);
    }
}
