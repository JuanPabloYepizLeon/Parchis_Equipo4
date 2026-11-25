package cliente;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import protocolo.Sobre;

/**
 * Guarda la última copia de la partida en el cliente y avisa a quien la observa cada vez que cambia.
 */
public final class ReplicaPartida {

    /** Eventos que genera el propio cliente; los del servidor están en {@link protocolo.Contrato.Eventos}. */
    public static final String CONEXION_PERDIDA = "CONEXION_PERDIDA";
    public static final String PARTIDA_RECUPERADA = "PARTIDA_RECUPERADA";

    /** Quien quiera enterarse de los cambios de la copia implementa esta interfaz. */
    public interface Observador {

        void alCambiar(String evento, PartidaVista partida);
    }

    private final List<Observador> observadores = new CopyOnWriteArrayList<>();
    private volatile Map<String, Object> crudo = Map.of();
    private volatile PartidaVista actual = PartidaVista.VACIA;

    public void agregarObservador(Observador o) {
        observadores.add(o);
    }

    public void quitarObservador(Observador o) {
        observadores.remove(o);
    }

    public PartidaVista actual() {
        return actual;
    }

    /** Snapshot tal como llegó del servidor; solo se usa para restaurar la partida en un nuevo anfitrión. */
    Map<String, Object> crudo() {
        return crudo;
    }

    public void aplicar(Sobre evento) {
        guardar(evento.payload());
        notificarLocal(evento.action());
    }

    public void sembrar(Sobre respuesta) {
        guardar(respuesta.payload());
    }

    void reemplazar(Map<String, Object> snapshot, String evento) {
        guardar(snapshot);
        notificarLocal(evento);
    }

    public void notificarLocal(String evento) {
        PartidaVista p = actual;
        for (Observador o : observadores) {
            o.alCambiar(evento, p);
        }
    }

    public void reiniciar() {
        observadores.clear();
        crudo = Map.of();
        actual = PartidaVista.VACIA;
    }

    private void guardar(Map<String, Object> snapshot) {
        crudo = snapshot;
        actual = PartidaVista.de(snapshot);
    }
}
