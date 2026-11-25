package controlador;

import cliente.HiloUi;
import cliente.SesionCliente;
import comun.ErrorNegocioException;
import comun.ValidacionException;
import modelo.Partida;
import vista.Navegador;
import vista.VistaConfigurarPartida;

/**
 * Controlador del caso de uso "Configurar partida": valida el puerto y la cantidad de jugadores y crea la partida.
 */
public class ConfigurarPartidaController {

    private final VistaConfigurarPartida vista;
    private final Navegador nav;
    private final SesionCliente sesion;

    public ConfigurarPartidaController(VistaConfigurarPartida vista, Navegador nav, SesionCliente sesion) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
    }

    public void crearPartida(String txtPuerto, String txtJugadores) {
        final int puerto;
        final int maxJugadores;
        try {
            if (txtPuerto.isBlank()) {
                throw new ValidacionException("puerto", "El puerto no puede estar vacío");
            }
            if (txtJugadores.isBlank()) {
                throw new ValidacionException("maxJugadores", "La cantidad de jugadores no puede estar vacía");
            }
            maxJugadores = parsear(txtJugadores, "Cantidad de jugadores no válida", "Ingresa un número entero entre 2 y 4.");
            if (maxJugadores < Partida.MIN_JUGADORES || maxJugadores > Partida.MAX_JUGADORES) {
                throw new ErrorNegocioException("Cantidad de jugadores no válida", "Debe ser de mínimo 2 y máximo 4.");
            }
            puerto = parsear(txtPuerto, "No se pudo crear la partida", "El puerto no es válido. Elige otro.");
            if (puerto < 1 || puerto > 65535) {
                throw new ErrorNegocioException("No se pudo crear la partida", "El puerto no es válido. Elige otro.");
            }
        } catch (ValidacionException e) {
            vista.marcarCampoError(e.campo, e.getMessage());
            return;
        } catch (ErrorNegocioException e) {
            vista.mostrarModal(e.titulo, e.getMessage());
            return;
        }

        sesion.hospedarPartida(puerto, maxJugadores)
                .thenRunAsync(nav::mostrarRegistroJugador, HiloUi.EDT)
                .exceptionallyAsync(ex -> {
                    sesion.cerrar();
                    return ErroresRed.aplicar(ex, vista::marcarCampoError, vista::mostrarModal);
                }, HiloUi.EDT);
    }

    private int parsear(String texto, String titulo, String mensaje) throws ErrorNegocioException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            throw new ErrorNegocioException(titulo, mensaje);
        }
    }
}
