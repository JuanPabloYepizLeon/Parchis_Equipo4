package vista;

import java.util.Set;
import modelo.Color;

public interface VistaRegistro {

    void mostrarColores(Set<Color> disponibles, Set<Color> ocupados);

    void marcarCampoError(String campo, String mensaje);

    void mostrarMensajeColor(String mensaje);
}
