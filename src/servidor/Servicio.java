package servidor;

import comun.ErrorNegocioException;
import comun.ValidacionException;
import java.util.Map;
import java.util.Set;

/**
 * Servicio que ofrece el servidor; tiene un nombre y varias acciones.
 */
public interface Servicio {

    String nombre();

    /** Acciones que expone el servicio; se publican en el catálogo de {@code sistema.describir}. */
    Set<String> acciones();

    Map<String, Object> invocar(String accion, Map<String, Object> payload, ContextoPeticion ctx)
            throws ErrorNegocioException, ValidacionException;
}
