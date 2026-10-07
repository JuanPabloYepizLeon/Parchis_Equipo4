package controlador;

import comun.ErrorNegocioException;
import comun.GestorRed;
import comun.Sesion;
import comun.ValidacionException;
import modelo.Partida;
import vista.Navegador;
import vista.VistaUnirse;

public class SolicitarUnirseController {

    private final VistaUnirse vista;
    private final Navegador nav;
    private final Sesion sesion;
    private final GestorRed red;

    public SolicitarUnirseController(VistaUnirse vista, Navegador nav, Sesion sesion, GestorRed red) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
        this.red = red;
    }

    public void solicitarUnirse(String txtDireccion, String txtPuerto) {
        try {
            if (txtDireccion.isBlank()) {
                throw new ValidacionException("direccion", "La dirección del servidor no puede estar vacía");
            }
            if (txtPuerto.isBlank()) {
                throw new ValidacionException("puerto", "El puerto no puede estar vacío");
            }

            int puerto;
            try {
                puerto = Integer.parseInt(txtPuerto.trim());
            } catch (NumberFormatException e) {
                throw new ErrorNegocioException("No se pudo unirse", "El puerto no es válido.");
            }

            Partida partida = red.solicitarAcceso(txtDireccion.trim(), puerto);
            sesion.setPartida(partida, false);
            nav.mostrarRegistroJugador();
        } catch (ValidacionException e) {
            vista.marcarCampoError(e.campo, e.getMessage());
        } catch (ErrorNegocioException e) {
            vista.mostrarModal("No se pudo unirse", "No fue posible conectar con el servidor o la partida ya está llena.");
        }
    }
}
