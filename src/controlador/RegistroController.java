package controlador;

import comun.ErrorNegocioException;
import comun.Sesion;
import comun.ValidacionException;
import java.util.EnumSet;
import java.util.Set;
import modelo.Color;
import modelo.Jugador;
import vista.Navegador;
import vista.VistaRegistro;

public class RegistroController {

    private final VistaRegistro vista;
    private final Navegador nav;
    private final Sesion sesion;

    public RegistroController(VistaRegistro vista, Navegador nav, Sesion sesion) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
    }

    public void consultarColoresDisponibles() {
        Set<Color> ocupados = sesion.getPartida().coloresOcupados();
        Set<Color> disponibles = EnumSet.complementOf(ocupados.isEmpty() ? EnumSet.noneOf(Color.class) : EnumSet.copyOf(ocupados));
        vista.mostrarColores(disponibles, ocupados);
    }

    public void registrarJugador(String txtNombre, Color txtColor, String txtAvatar) {
        try {
            if (txtNombre == null || txtNombre.isBlank()) {
                throw new ValidacionException("nombre", "El nombre no puede estar vacío");
            }
            if (txtColor == null) {
                throw new ValidacionException("color", "Elige un color");
            }
            if (txtAvatar == null) {
                throw new ValidacionException("avatar", "Elige un avatar");
            }
            if (sesion.getPartida().coloresOcupados().contains(txtColor)) {
                throw new ErrorNegocioException("Color ocupado", "Ese color ya está ocupado. Elige otro.");
            }

            Jugador jugador = new Jugador(txtNombre.trim(), txtColor, txtAvatar);
            sesion.getPartida().agregarJugador(jugador);
            sesion.setJugadorLocal(jugador);
            nav.mostrarSalaEspera();
        } catch (ValidacionException e) {
            vista.marcarCampoError(e.campo, e.getMessage());
        } catch (ErrorNegocioException e) {
            vista.mostrarMensajeColor(e.getMessage());
            consultarColoresDisponibles();
        }
    }
}
