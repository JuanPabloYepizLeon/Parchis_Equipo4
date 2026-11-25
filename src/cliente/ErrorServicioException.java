package cliente;

import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import protocolo.Sobre;

/**
 * Error que devolvió el servidor al responder una petición (por ejemplo, "No es tu turno").
 */
public final class ErrorServicioException extends RuntimeException {

    private final String codigo;
    private final String titulo;
    private final String campo;

    public ErrorServicioException(Sobre respuesta) {
        super(texto(respuesta.error(), "message"));
        this.codigo = texto(respuesta.error(), "code");
        this.titulo = texto(respuesta.error(), "title");
        this.campo = texto(respuesta.error(), "field");
    }

    private static String texto(Map<String, Object> e, String k) {
        return e == null || e.get(k) == null ? null : String.valueOf(e.get(k));
    }

    public String codigo() {
        return codigo;
    }

    public String titulo() {
        return titulo;
    }

    public String campo() {
        return campo;
    }

    public static Throwable causa(Throwable t) {
        while ((t instanceof CompletionException || t instanceof ExecutionException) && t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }
}
