package servidor;

import comun.ErrorNegocioException;
import modelo.Color;
import modelo.Jugador;
import modelo.Partida;
import java.util.Map;

/**
 * Guarda la partida del servidor y la comparte entre todos los servicios.
 */
public final class ContextoServidor {

    private ServidorTcp servidor;
    private Partida partida;

    void vincular(ServidorTcp servidor) {
        this.servidor = servidor;
        servidor.alDesconectar(this::conexionPerdida);
    }

    private void conexionPerdida(ConexionCliente conexion) {
        Partida p;
        synchronized (this) {
            p = partida;
        }
        Jugador j = conexion.jugador();
        if (p != null && j != null) {
            p.retirarJugador(j);
        }
    }

    public synchronized Partida restaurarPartida(Map<String, Object> snapshot, String colorNuevoAnfitrion)
            throws ErrorNegocioException {
        if (partida != null) {
            throw new ErrorNegocioException("No se pudo restaurar", "Este servidor ya tiene una partida.");
        }
        Partida p = RestauradorPartida.restaurar(snapshot);
        p.agregarObservador(new PublicadorEventos(servidor));
        Jugador anterior = p.getAnfitrion();
        Jugador nuevo = p.buscarJugador(Color.valueOf(colorNuevoAnfitrion));
        if (nuevo == null) {
            throw new ErrorNegocioException("No se pudo restaurar", "El nuevo anfitrión no pertenece a la partida.");
        }
        p.setAnfitrion(nuevo);
        partida = p;
        if (anterior != null && anterior != nuevo) {
            p.retirarJugador(anterior);
        }
        return p;
    }

    public synchronized Partida crearPartida(int maxJugadores) throws ErrorNegocioException {
        if (partida != null) {
            throw new ErrorNegocioException("No se pudo crear la partida", "Este servidor ya tiene una partida.");
        }
        partida = new Partida(maxJugadores);
        partida.agregarObservador(new PublicadorEventos(servidor));
        return partida;
    }

    public synchronized Partida partidaObligatoria() throws ErrorNegocioException {
        if (partida == null) {
            throw new ErrorNegocioException("Sin partida", "Todavía no hay una partida creada en este servidor.");
        }
        return partida;
    }
}
