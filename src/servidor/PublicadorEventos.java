package servidor;

import java.util.concurrent.atomic.AtomicLong;
import modelo.Evento;
import modelo.Partida;
import modelo.PartidaObserver;
import protocolo.Sobre;

/**
 * Avisa a todos los jugadores conectados cada vez que la partida cambia.
 */
public final class PublicadorEventos implements PartidaObserver {

    private final ServidorTcp servidor;
    private final AtomicLong secuencia = new AtomicLong();

    public PublicadorEventos(ServidorTcp servidor) {
        this.servidor = servidor;
    }

    @Override
    public void alCambiar(Evento evento, Partida partida) {
        servidor.difundir(Sobre.evento("s-" + secuencia.incrementAndGet(), "partida",
                evento.name(), SnapshotPartida.de(partida)));
    }
}
