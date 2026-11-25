package controlador;

import cliente.HiloUi;
import cliente.PartidaVista;
import cliente.ReplicaPartida;
import cliente.SesionCliente;
import modelo.EstadoPartida;
import protocolo.Contrato.Eventos;
import vista.Navegador;
import vista.VistaTableroRed;

/**
 * Controlador del caso de uso "Ejercer turno": lanza el dado, mueve fichas y actualiza el tablero
 * cada vez que la partida cambia.
 */
public class EjercerTurnoRedController implements ReplicaPartida.Observador {

    private final VistaTableroRed vista;
    private final Navegador nav;
    private final SesionCliente sesion;

    public EjercerTurnoRedController(VistaTableroRed vista, Navegador nav, SesionCliente sesion) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
        sesion.replica().agregarObservador(this);
        mostrar(sesion.replica().actual());
    }

    public void lanzarDado() {
        sesion.remoto().lanzarDado().thenAcceptAsync(r -> {
            if (r.pierdeTurno()) {
                vista.mostrarMensaje("Cuarto 6 consecutivo: pierdes el turno.");
            } else if (r.sinMovimientos()) {
                vista.mostrarMensaje("Sacaste " + r.valor() + ": no tienes movimientos posibles.");
            }
            // Si hay movimientos, el evento DADO_LANZADO ya mostró las fichas movibles.
        }, HiloUi.EDT).exceptionallyAsync(this::error, HiloUi.EDT);
    }

    public void moverFicha(int idFicha) {
        // El resultado llega por los eventos de la partida (incluida una posible bonificación).
        sesion.remoto().moverFicha(idFicha).exceptionallyAsync(ex -> {
            error(ex);
            return null;
        }, HiloUi.EDT);
    }

    @Override
    public void alCambiar(String evento, PartidaVista partida) {
        String quien = partida.nombreDe(partida.ultimoJugador());
        switch (evento) {
            case Eventos.DADO_LANZADO ->
                vista.mostrarMensaje(quien + " lanzó el dado: " + partida.ultimoDado());
            case Eventos.FICHA_CAPTURADA ->
                vista.mostrarMensaje("¡Captura de " + quien + "! Una ficha rival vuelve a su base.");
            case Eventos.FICHA_EN_META ->
                vista.mostrarMensaje(quien + " metió una ficha en la meta.");
            case Eventos.TURNO_PERDIDO ->
                vista.mostrarMensaje(quien + " sacó el cuarto 6 consecutivo y pierde el turno.");
            case Eventos.TURNO_CAMBIADO ->
                vista.mostrarMensaje("Turno de: " + partida.nombreDe(partida.turno()));
            case Eventos.JUGADOR_ABANDONO ->
                vista.mostrarMensaje(quien + " abandonó la partida. Sus fichas fueron retiradas.");
            case ReplicaPartida.CONEXION_PERDIDA ->
                vista.mostrarMensaje("Se perdió la conexión con el anfitrión. Reconectando…");
            case ReplicaPartida.PARTIDA_RECUPERADA -> {
                if (partida.estado() != EstadoPartida.TERMINADA) {
                    vista.mostrarMensaje("Conexión recuperada. Nuevo anfitrión: " + partida.nombreDe(partida.anfitrion()));
                }
            }
            default -> {
            }
        }
        mostrar(partida);
        boolean terminada = Eventos.PARTIDA_TERMINADA.equals(evento)
                || (ReplicaPartida.PARTIDA_RECUPERADA.equals(evento) && partida.estado() == EstadoPartida.TERMINADA);
        if (terminada) {
            terminar(partida);
        }
    }

    private void mostrar(PartidaVista partida) {
        vista.mostrarPartida(partida, sesion.colorLocal());
        if (partida.esMiTurnoDeMover(sesion.colorLocal())) {
            vista.mostrarFichasMovibles(partida.movibles(), partida.pasosPendientes());
        }
    }

    private void terminar(PartidaVista partida) {
        sesion.replica().quitarObservador(this);
        String texto;
        if (partida.ganador() == null) {
            texto = "La partida terminó: no quedan jugadores activos.";
        } else if ("ABANDONO".equals(partida.motivoFin())) {
            texto = "Ganador: " + partida.nombreDe(partida.ganador()) + " (único jugador activo)";
        } else {
            texto = "Ganador: " + partida.nombreDe(partida.ganador()) + " (4 fichas en meta)";
        }
        vista.mostrarFinPartida(texto);
    }

    public void volverAlInicio() {
        sesion.cerrar();
        nav.mostrarInicio();
    }

    private Void error(Throwable ex) {
        return ErroresRed.aplicar(ex, (c, m) -> vista.mostrarError("Error", m), vista::mostrarError);
    }
}
