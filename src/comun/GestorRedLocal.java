package comun;

import comun.Sesion;
import java.util.Set;
import modelo.Evento;
import modelo.Partida;

public class GestorRedLocal implements GestorRed {

    private static final Set<Integer> PUERTOS_OCUPADOS = Set.of(80, 443, 8080);
    private final Sesion sesionAnfitrion;

    public GestorRedLocal(Sesion sesionAnfitrion) {
        this.sesionAnfitrion = sesionAnfitrion;
    }

    @Override
    public void abrirServidor(int puerto) throws ErrorNegocioException {
        if (PUERTOS_OCUPADOS.contains(puerto)) {
            throw new ErrorNegocioException("No se pudo crear la partida", "El puerto ya está en uso o no es válido. Elige otro puerto e inténtalo de nuevo.");
        }
    }

    @Override
    public Partida solicitarAcceso(String direccion, int puerto) throws ErrorNegocioException {
        Partida partida = sesionAnfitrion.getPartida();
        boolean direccionOk = direccion.equals("localhost") || direccion.equals("127.0.0.1");
        boolean hayServidor = partida != null && partida.getPuerto() == puerto;

        if (!direccionOk || !hayServidor || !partida.solicitarAcceso()) {
            throw new ErrorNegocioException("No se pudo unirse", "Sin respuesta o partida llena.");
        }
        return partida;
    }

    @Override
    public void difundir(Evento evento, Partida partida) {
        // No hace nada: al compartir el mismo objeto Partida, los observadores ya se enteran.
    }
}
