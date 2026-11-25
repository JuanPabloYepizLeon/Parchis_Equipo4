package modelo;

/**
 * Quien quiera enterarse de los cambios de la partida implementa esta interfaz.
 */
public interface PartidaObserver {

    void alCambiar(Evento evento, Partida partida);
}
