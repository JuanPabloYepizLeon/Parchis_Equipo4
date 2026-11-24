package comun;

import java.util.List;

/**
 * Servidor que está corriendo en el equipo del anfitrión.
 */
public interface ServidorHospedado extends AutoCloseable {

    int puerto();

    /** Direcciones IPv4 de la red local por las que otros jugadores pueden conectarse. */
    List<String> direccionesLocales();

    @Override
    void close();
}
