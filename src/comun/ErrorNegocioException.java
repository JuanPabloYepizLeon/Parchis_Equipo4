package comun;

/**
 * Error cuando se rompe una regla del juego (por ejemplo, color ocupado o partida llena).
 */
public class ErrorNegocioException extends Exception {

    public final String titulo;
    
    public ErrorNegocioException(String titulo, String mensaje) {
        super(mensaje);
        this.titulo = titulo;
    }
    
}
