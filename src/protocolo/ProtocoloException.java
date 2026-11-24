package protocolo;

/**
 * Error cuando un mensaje no tiene el formato correcto o pide algo que no existe.
 */
public class ProtocoloException extends RuntimeException {

    private final String codigo;

    public ProtocoloException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
