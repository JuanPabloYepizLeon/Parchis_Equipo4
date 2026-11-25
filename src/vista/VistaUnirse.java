package vista;

/**
 * Lo que la pantalla de unirse a partida debe poder mostrar.
 */
public interface VistaUnirse {

    void marcarCampoError(String campo, String mensaje);

    void mostrarModal(String titulo, String mensaje);
}
