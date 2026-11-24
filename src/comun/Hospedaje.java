package comun;

import java.util.Map;

/**
 * Permite arrancar el servidor en el equipo del anfitrión sin que el cliente conozca cómo está hecho.
 */
public interface Hospedaje {

    ServidorHospedado arrancar(int puerto) throws ErrorNegocioException;

    /** Arranca el servidor y restaura la partida del snapshot con un nuevo anfitrión (reconexión). */
    ServidorHospedado arrancarRestaurando(int puerto, Map<String, Object> snapshot, String colorNuevoAnfitrion)
            throws ErrorNegocioException;
}
