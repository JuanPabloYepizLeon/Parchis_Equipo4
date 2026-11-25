package controlador;

import cliente.ErrorServicioException;
import comun.ErrorNegocioException;
import comun.ValidacionException;
import java.util.function.BiConsumer;
import protocolo.Contrato.Errores;

/**
 * Muestra en la vista los errores que llegan del servidor: en el campo afectado o en un mensaje.
 */
final class ErroresRed {

    private ErroresRed() {
    }

    static Void aplicar(Throwable ex, BiConsumer<String, String> campoEnRojo, BiConsumer<String, String> modal) {
        Throwable c = ErrorServicioException.causa(ex);
        if (c instanceof ErrorServicioException es) {
            if (Errores.VALIDACION.equals(es.codigo()) && es.campo() != null) {
                campoEnRojo.accept(es.campo(), es.getMessage());
            } else {
                modal.accept(es.titulo() == null ? "Error" : es.titulo(), es.getMessage());
            }
        } else if (c instanceof ErrorNegocioException en) {
            modal.accept(en.titulo, en.getMessage());
        } else if (c instanceof ValidacionException ve) {
            campoEnRojo.accept(ve.campo, ve.getMessage());
        } else {
            modal.accept("Error de conexión", "No se pudo comunicar con el servidor.");
        }
        return null;
    }
}
