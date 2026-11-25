package servidor;

import comun.ErrorNegocioException;
import modelo.Jugador;

/**
 * Datos de quién hizo una petición: su conexión y el jugador que tiene asociado.
 * El servidor reconoce al jugador por su conexión, que se mantiene abierta toda la partida.
 */
public final class ContextoPeticion {

    private final ConexionCliente conexion;

    public ContextoPeticion(ConexionCliente conexion) {
        this.conexion = conexion;
    }

    public ConexionCliente conexion() {
        return conexion;
    }

    public Jugador jugadorObligatorio() throws ErrorNegocioException {
        Jugador j = conexion.jugador();
        if (j == null) {
            throw new ErrorNegocioException("No registrado", "Primero debes registrarte como jugador.");
        }
        return j;
    }
}
