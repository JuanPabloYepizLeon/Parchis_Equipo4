package controlador;

import cliente.HiloUi;
import cliente.SesionCliente;
import vista.VistaSalaEspera;

/**
 * Controlador del caso de uso "Iniciar partida" (solo lo usa el anfitrión).
 */
public class IniciarPartidaController {

    private final VistaSalaEspera vista;
    private final SesionCliente sesion;

    public IniciarPartidaController(VistaSalaEspera vista, SesionCliente sesion) {
        this.vista = vista;
        this.sesion = sesion;
    }

    public void iniciarPartida() {
        sesion.remoto().iniciarPartida()
                .exceptionallyAsync(ex -> {
                    ErroresRed.aplicar(ex, (c, m) -> vista.mostrarModal("No se puede iniciar", m), vista::mostrarModal);
                    return null;
                }, HiloUi.EDT);
    }
}
