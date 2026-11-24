package comun;

/**
 * Error cuando un dato escrito por el usuario no es válido; indica qué campo está mal.
 */
public class ValidacionException extends Exception {

    public final String campo;

    public ValidacionException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }
}
