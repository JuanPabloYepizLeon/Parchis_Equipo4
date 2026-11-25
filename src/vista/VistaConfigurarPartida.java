package vista;

/**
 * Lo que la pantalla de configurar partida debe poder mostrar.
 */
public interface VistaConfigurarPartida {

    void marcarCampoError(String campo, String mensaje);

    void mostrarModal(String titulo, String mensaje);
}
