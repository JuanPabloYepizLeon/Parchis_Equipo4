package comun;

public class ErrorNegocioException extends Exception {

    public final String titulo;
    
    public ErrorNegocioException(String titulo, String mensaje) {
        super(mensaje);
        this.titulo = titulo;
    }
    
}
