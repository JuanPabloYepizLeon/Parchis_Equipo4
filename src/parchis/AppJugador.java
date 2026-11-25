package parchis;

import cliente.SesionCliente;
import cliente.HiloUi;
import cliente.ReplicaPartida;
import controlador.AbandonarPartidaController;
import controlador.ConfigurarPartidaController;
import controlador.EjercerTurnoRedController;
import controlador.IniciarPartidaController;
import controlador.RegistroController;
import controlador.SolicitarUnirseController;
import controlador.SolicitudInicioController;
import java.awt.Window;
import java.util.List;
import javax.swing.JOptionPane;
import vista.Navegador;
import vista.swing.VentanaConfigurarPartida;
import vista.swing.VentanaInicio;
import vista.swing.VentanaRegistro;
import vista.swing.VentanaSalaEspera;
import vista.swing.VentanaTablero;
import vista.swing.VentanaUnirse;

/**
 * Aplicación del jugador: cambia entre las ventanas del juego y crea su controlador.
 */
public class AppJugador implements Navegador {

    private final String nombre;
    private final SesionCliente sesion;
    private Window actual;
    private boolean direccionAvisada;

    public AppJugador(String nombre, SesionCliente sesion) {
        this.nombre = nombre;
        this.sesion = sesion;
        sesion.alPerderConexion(this::alPerderConexion);
    }

    private void alPerderConexion() {
        sesion.replica().notificarLocal(ReplicaPartida.CONEXION_PERDIDA);
        sesion.recuperarPartida()
                .thenAcceptAsync(recuperada -> {
                    if (!recuperada) {
                        conexionPerdida();
                    }
                }, HiloUi.EDT)
                .exceptionallyAsync(ex -> {
                    conexionPerdida();
                    return null;
                }, HiloUi.EDT);
    }

    private void conexionPerdida() {
        JOptionPane.showMessageDialog(actual, "Se perdió la conexión con el servidor.", "Conexión", JOptionPane.WARNING_MESSAGE);
        sesion.cerrar();
        mostrarInicio();
    }

    public void iniciar() {
        mostrarInicio();
    }

    @Override
    public void mostrarInicio() {
        direccionAvisada = false;
        var ventana = new VentanaInicio("Parchís - " + nombre, this::mostrarConfigurar, this::mostrarUnirse);
        mostrarVentana(ventana, ventana::mostrar);
    }

    void mostrarConfigurar() {
        var vista = new VentanaConfigurarPartida();
        vista.setControlador(new ConfigurarPartidaController(vista, this, sesion));
        mostrarVentana(vista, vista::mostrar);
    }

    void mostrarUnirse() {
        var vista = new VentanaUnirse();
        vista.setControlador(new SolicitarUnirseController(vista, this, sesion));
        mostrarVentana(vista, vista::mostrar);
    }

    @Override
    public void mostrarRegistroJugador() {
        var vista = new VentanaRegistro();
        var cu2 = new RegistroController(vista, this, sesion);
        vista.setControlador(cu2);
        mostrarVentana(vista, vista::mostrar);
        cu2.consultarColoresDisponibles();
        avisarDireccion();
    }

    private void avisarDireccion() {
        if (!sesion.esAnfitrion() || direccionAvisada || sesion.servidorLocal() == null) {
            return;
        }
        direccionAvisada = true;
        List<String> ips = sesion.servidorLocal().direccionesLocales();
        String texto = "Tu partida está activa. Los demás jugadores deben usar:\n\n"
                + "Puerto: " + sesion.servidorLocal().puerto() + "\nDirección:\n  "
                + (ips.isEmpty() ? "(consúltala con ipconfig / ifconfig)" : String.join("\n  ", ips));
        JOptionPane.showMessageDialog(actual, texto, "Datos para unirse", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarSalaEspera() {
        var vista = new VentanaSalaEspera(sesion.colorLocal(), sesion.esAnfitrion());
        var cu5 = new SolicitudInicioController(vista, this, sesion);
        var cu3 = new IniciarPartidaController(vista, sesion);
        vista.setControladores(cu5, cu3);
        mostrarVentana(vista, vista::mostrar);
    }

    @Override
    public void mostrarTablero() {
        var vista = new VentanaTablero();
        vista.setControlador(new EjercerTurnoRedController(vista, this, sesion));
        vista.setControladorAbandono(new AbandonarPartidaController(vista, this, sesion));
        mostrarVentana(vista, vista::mostrar);
    }

    private void mostrarVentana(Window nueva, Runnable mostrar) {
        Window anterior = actual;
        actual = nueva;
        mostrar.run();
        if (anterior != null) {
            anterior.dispose();
        }
    }
}
