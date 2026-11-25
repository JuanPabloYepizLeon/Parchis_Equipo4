package vista;

import cliente.PartidaVista.JugadorVista;
import java.util.List;
import modelo.EstadoJugador;

/**
 * Lo que la sala de espera debe poder mostrar.
 */
public interface VistaSalaEspera {

    void mostrarJugadores(List<JugadorVista> jugadores);

    void mostrarEstadoLocal(EstadoJugador estado);

    void habilitarIniciar(boolean habilitado);

    void mostrarModal(String titulo, String mensaje);
}
