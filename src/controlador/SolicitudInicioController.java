package controlador;

import cliente.HiloUi;
import cliente.PartidaVista;
import cliente.ReplicaPartida;
import cliente.SesionCliente;
import modelo.EstadoJugador;
import modelo.EstadoPartida;
import protocolo.Contrato.Eventos;
import vista.Navegador;
import vista.VistaSalaEspera;

/**
 * Controlador de la sala de espera: marca al jugador como listo y muestra quién está listo.
 */
public class SolicitudInicioController implements ReplicaPartida.Observador {

    private final VistaSalaEspera vista;
    private final Navegador nav;
    private final SesionCliente sesion;

    public SolicitudInicioController(VistaSalaEspera vista, Navegador nav, SesionCliente sesion) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
        sesion.replica().agregarObservador(this);
        refrescar(sesion.replica().actual());
    }

    public void solicitarInicio() {
        cambiar(true);
    }

    public void cancelarListo() {
        cambiar(false);
    }

    private void cambiar(boolean listo) {
        sesion.remoto().marcarListo(listo)
                .thenAcceptAsync(r -> vista.mostrarEstadoLocal(listo ? EstadoJugador.LISTO : EstadoJugador.ESPERANDO), HiloUi.EDT)
                .exceptionallyAsync(ex -> ErroresRed.aplicar(ex, (c, m) -> vista.mostrarModal("Error", m), vista::mostrarModal),
                        HiloUi.EDT);
    }

    @Override
    public void alCambiar(String evento, PartidaVista partida) {
        if (Eventos.PARTIDA_INICIADA.equals(evento)) {
            sesion.replica().quitarObservador(this);
            nav.mostrarTablero();
            return;
        }
        if (ReplicaPartida.PARTIDA_RECUPERADA.equals(evento)) {
            // Cambió el anfitrión: se reconstruye la pantalla para que muestre (u oculte) Iniciar Partida.
            sesion.replica().quitarObservador(this);
            if (partida.estado() == EstadoPartida.EN_JUEGO) {
                nav.mostrarTablero();
            } else {
                nav.mostrarSalaEspera();
            }
            return;
        }
        refrescar(partida);
    }

    private void refrescar(PartidaVista partida) {
        vista.mostrarJugadores(partida.jugadores());
        PartidaVista.JugadorVista yo = partida.jugador(sesion.colorLocal());
        if (yo != null) {
            vista.mostrarEstadoLocal(yo.estado());
        }
        vista.habilitarIniciar(sesion.esAnfitrion() && partida.todosListos());
    }
}
