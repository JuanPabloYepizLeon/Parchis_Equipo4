package servidor;

import comun.ErrorNegocioException;
import comun.ValidacionException;
import java.util.Map;

/**
 * Una acción de un servicio (por ejemplo, "lanzarDado" del servicio turno).
 */
@FunctionalInterface
public interface Accion {

    Map<String, Object> ejecutar(Map<String, Object> payload, ContextoPeticion ctx)
            throws ErrorNegocioException, ValidacionException;
}
