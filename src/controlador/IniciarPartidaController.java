package controlador;

import comun.GestorRed;
import comun.Sesion;
import modelo.EstadoPartida;
import modelo.Evento;
import modelo.Partida;
import vista.Navegador;
import vista.VistaSalaEspera;

public class IniciarPartidaController {

    private final VistaSalaEspera vista;
    private final Navegador nav;
    private final Sesion sesion;
    private final GestorRed red;

    public IniciarPartidaController(VistaSalaEspera vista, Navegador nav, Sesion sesion, GestorRed red) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
        this.red = red;
    }

    public void iniciarPartida() {
        Partida partida = sesion.getPartida();
        if (!sesion.esAnfitrion() || !partida.todosListos()) {
            vista.mostrarModal("No se puede iniciar", "Un jugador dejó de estar listo o se desconectó. Revisa el estado e inténtalo de nuevo.");
            return;
        }
        partida.elegirJugadorInicial();
        partida.cambiarEstado(EstadoPartida.EN_JUEGO);
        red.difundir(Evento.PARTIDA_INICIADA, partida);
        nav.mostrarTablero();
    }
}
