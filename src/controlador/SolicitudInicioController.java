package controlador;

import comun.Sesion;
import modelo.EstadoJugador;
import modelo.Evento;
import modelo.Jugador;
import modelo.Partida;
import modelo.PartidaObserver;
import vista.Navegador;
import vista.VistaSalaEspera;

public class SolicitudInicioController implements PartidaObserver {

    private final VistaSalaEspera vista;
    private final Navegador nav;
    private final Sesion sesion;

    public SolicitudInicioController(VistaSalaEspera vista, Navegador nav, Sesion sesion) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
        sesion.getPartida().agregarObservador(this);
    }

    public void solicitarInicio() {
        cambiar(EstadoJugador.LISTO);
    }

    public void cancelarListo() {
        cambiar(EstadoJugador.ESPERANDO);
    }

    private void cambiar(EstadoJugador estado) {
        Jugador jugador = sesion.getJugadorLocal();
        jugador.cambiarEstado(estado);
        sesion.getPartida().jugadorCambioEstado();
        vista.mostrarEstadoLocal(estado);
    }

    @Override
    public void alCambiar(Evento evento, Partida partida) {
        if (evento == Evento.PARTIDA_INICIADA) {
            partida.quitarObservador(this);
            if (!sesion.esAnfitrion()) {
                nav.mostrarTablero();
            }
            return;
        }
        vista.mostrarJugadores(partida.getJugadores());
        vista.habilitarIniciar(sesion.esAnfitrion() && partida.todosListos());
    }
}
