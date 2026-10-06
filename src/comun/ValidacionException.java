package comun;

public class ValidacionException extends Exception {

    public final String campo;

    public ValidacionException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }
}
