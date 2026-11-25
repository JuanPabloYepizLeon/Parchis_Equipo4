package vista;

import java.util.Set;
import modelo.Color;

/**
 * Lo que la pantalla de registro de jugador debe poder mostrar.
 */
public interface VistaRegistro {

    void mostrarColores(Set<Color> disponibles, Set<Color> ocupados);

    void marcarCampoError(String campo, String mensaje);

    void mostrarMensajeColor(String mensaje);
}
