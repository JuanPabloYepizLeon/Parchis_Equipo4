package vista;

import java.util.List;
import modelo.Ficha;

public interface VistaTablero {

    void mostrarFichasMovibles(List<Ficha> movibles, int pasos);

    void mostrarError(String titulo, String mensaje);
}
