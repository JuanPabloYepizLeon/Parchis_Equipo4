package controlador;

import cliente.HiloUi;
import cliente.SesionCliente;
import vista.Navegador;
import vista.VistaAbandonarPartida;

/**
 * Controlador del caso de uso "Abandonar partida".
 */
public class AbandonarPartidaController {

    private final VistaAbandonarPartida vista;
    private final Navegador nav;
    private final SesionCliente sesion;

    public AbandonarPartidaController(VistaAbandonarPartida vista, Navegador nav, SesionCliente sesion) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
    }

    public void abandonar() {
        sesion.remoto().abandonar()
                .thenAcceptAsync(r -> {
                    sesion.cerrar();
                    nav.mostrarInicio();
                }, HiloUi.EDT)
                .exceptionallyAsync(ex -> ErroresRed.aplicar(ex, (c, m) -> vista.mostrarError("Error", m), vista::mostrarError),
                        HiloUi.EDT);
    }
}
