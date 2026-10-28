package controlador;

import comun.ErrorNegocioException;
import comun.GestorRed;
import comun.Sesion;
import modelo.EstadoPartida;
import modelo.Evento;
import modelo.FaseTurno;
import modelo.Partida;
import modelo.ResultadoLanzamiento;
import vista.VistaTablero;

public class EjercerTurnoController {

    private final VistaTablero vista;
    private final Sesion sesion;
    private final GestorRed red;

    public EjercerTurnoController(VistaTablero vista, Sesion sesion, GestorRed red) {
        this.vista = vista;
        this.sesion = sesion;
        this.red = red;
    }

    public void lanzarDado() {
        Partida partida = sesion.getPartida();
        try {
            ResultadoLanzamiento r = partida.lanzarDado(sesion.getJugadorLocal());
            red.difundir(Evento.DADO_LANZADO, partida);
            if (!r.pierdeTurno()) {
                vista.mostrarFichasMovibles(r.movibles(), r.valor());
            }
        } catch (ErrorNegocioException e) {
            vista.mostrarError(e.titulo, e.getMessage());
        }
    }

    public void moverFicha(int idFicha) {
        Partida partida = sesion.getPartida();
        try {
            partida.moverFicha(sesion.getJugadorLocal(), idFicha);
            red.difundir(Evento.FICHA_MOVIDA, partida);
            if (partida.getEstado() == EstadoPartida.TERMINADA) {
                red.difundir(Evento.PARTIDA_TERMINADA, partida);
            } else if (partida.getFase() == FaseTurno.ESPERANDO_MOVIMIENTO && partida.getJugadorEnTurno() == sesion.getJugadorLocal()) {
                vista.mostrarFichasMovibles(partida.getFichasMovibles(), partida.getPasosPendientes());
            }
        } catch (ErrorNegocioException e) {
            vista.mostrarError(e.titulo, e.getMessage());
        }
    }
}
