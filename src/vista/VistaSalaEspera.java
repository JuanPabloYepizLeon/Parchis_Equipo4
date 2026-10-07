package vista;

import java.util.List;
import modelo.EstadoJugador;
import modelo.Jugador;

public interface VistaSalaEspera {

    void mostrarJugadores(List<Jugador> jugadores);

    void mostrarEstadoLocal(EstadoJugador estado);

    void habilitarIniciar(boolean habilitado);

    void mostrarModal(String titulo, String mensaje);
}
