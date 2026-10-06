package comun;

import modelo.Jugador;
import modelo.Partida;

public class Sesion {

    private Partida partida;
    private Jugador jugadorLocal;
    private boolean anfitrion;

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida, boolean esAnfitrion) {
        this.partida = partida;
        this.anfitrion = esAnfitrion;
    }

    public Jugador getJugadorLocal() {
        return jugadorLocal;
    }

    public void setJugadorLocal(Jugador jugadorLocal) {
        this.jugadorLocal = jugadorLocal;
    }

    public boolean esAnfitrion() {
        return anfitrion;
    }

}
