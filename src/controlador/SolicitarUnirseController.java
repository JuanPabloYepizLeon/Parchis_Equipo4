package controlador;

import cliente.HiloUi;
import cliente.SesionCliente;
import vista.Navegador;
import vista.VistaUnirse;

/**
 * Controlador del caso de uso "Unirse a partida": valida la dirección y el puerto y se conecta.
 */
public class SolicitarUnirseController {

    private final VistaUnirse vista;
    private final Navegador nav;
    private final SesionCliente sesion;

    public SolicitarUnirseController(VistaUnirse vista, Navegador nav, SesionCliente sesion) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
    }

    public void solicitarUnirse(String txtDireccion, String txtPuerto) {
        if (txtDireccion.isBlank()) {
            vista.marcarCampoError("direccion", "La dirección del servidor no puede estar vacía");
            return;
        }
        if (txtPuerto.isBlank()) {
            vista.marcarCampoError("puerto", "El puerto no puede estar vacío");
            return;
        }
        final int puerto;
        try {
            puerto = Integer.parseInt(txtPuerto.trim());
        } catch (NumberFormatException e) {
            vista.mostrarModal("No se pudo unirse", "El puerto no es válido.");
            return;
        }

        sesion.unirse(txtDireccion.trim(), puerto)
                .thenRunAsync(nav::mostrarRegistroJugador, HiloUi.EDT)
                .exceptionallyAsync(ex -> {
                    sesion.cerrar();
                    vista.mostrarModal("No se pudo unirse",
                            "No fue posible conectar con el servidor o la partida ya está llena.");
                    return null;
                }, HiloUi.EDT);
    }
}
