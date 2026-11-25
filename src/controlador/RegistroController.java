package controlador;

import cliente.ErrorServicioException;
import cliente.HiloUi;
import cliente.SesionCliente;
import java.util.EnumSet;
import java.util.Set;
import modelo.Color;
import protocolo.Contrato.Errores;
import vista.Navegador;
import vista.VistaRegistro;

/**
 * Controlador del caso de uso "Registrar jugador": muestra los colores libres y registra al jugador.
 */
public class RegistroController {

    private final VistaRegistro vista;
    private final Navegador nav;
    private final SesionCliente sesion;

    public RegistroController(VistaRegistro vista, Navegador nav, SesionCliente sesion) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
    }

    public void consultarColoresDisponibles() {
        Set<Color> ocupados = sesion.replica().actual().coloresOcupados();
        Set<Color> disponibles = EnumSet.allOf(Color.class);
        disponibles.removeAll(ocupados);
        vista.mostrarColores(disponibles, ocupados);
    }

    public void registrarJugador(String txtNombre, Color color, String avatar) {
        String nombre = txtNombre == null ? null : txtNombre.trim();
        sesion.remoto().registrar(nombre, color == null ? null : color.name(), avatar)
                .thenAcceptAsync(r -> {
                    sesion.jugadorLocal(nombre, color);
                    sesion.replica().sembrar(r);
                    nav.mostrarSalaEspera();
                }, HiloUi.EDT)
                .exceptionallyAsync(ex -> {
                    Throwable c = ErrorServicioException.causa(ex);
                    if (c instanceof ErrorServicioException es && Errores.VALIDACION.equals(es.codigo())) {
                        vista.marcarCampoError(es.campo(), es.getMessage());
                    } else if (c instanceof ErrorServicioException es) {
                        vista.mostrarMensajeColor(es.getMessage());
                        refrescarColores();
                    } else {
                        vista.mostrarMensajeColor("No se pudo comunicar con el servidor.");
                    }
                    return null;
                }, HiloUi.EDT);
    }

    private void refrescarColores() {
        sesion.remoto().estado().thenAcceptAsync(s -> {
            sesion.replica().sembrar(s);
            consultarColoresDisponibles();
        }, HiloUi.EDT);
    }
}
