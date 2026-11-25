package vista;

import cliente.PartidaVista;
import java.util.List;
import modelo.Color;

/**
 * Lo que la pantalla del tablero debe poder mostrar.
 */
public interface VistaTableroRed {

    void mostrarPartida(PartidaVista partida, Color miColor);

    void mostrarFichasMovibles(List<Integer> idsFicha, int pasos);

    void mostrarMensaje(String texto);

    void mostrarError(String titulo, String mensaje);

    void mostrarFinPartida(String texto);
}
