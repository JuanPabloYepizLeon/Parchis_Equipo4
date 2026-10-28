package controlador;

import comun.ErrorNegocioException;
import comun.GestorRed;
import comun.Sesion;
import comun.ValidacionException;
import modelo.Partida;
import vista.Navegador;
import vista.VistaConfigurarPartida;

public class ConfigurarPartidaController {

    private final VistaConfigurarPartida vista;
    private final Navegador nav;
    private final Sesion sesion;
    private final GestorRed red;

    public ConfigurarPartidaController(VistaConfigurarPartida vista, Navegador nav, Sesion sesion, GestorRed red) {
        this.vista = vista;
        this.nav = nav;
        this.sesion = sesion;
        this.red = red;
    }

    public void crearPartida(String txtPuerto, String txtJugadores) {
        try {
            if (txtPuerto.isBlank()) {
                throw new ValidacionException("puerto", "El puerto no puede estar vacío");
            }
            if (txtJugadores.isBlank()) {
                throw new ValidacionException("maxJugadores", "La cantidad de jugadores no puede estar vacía");
            }

            int maxJugadores = parsear(txtJugadores, "Cantidad de jugadores no válida", "Ingresa un número entero entre 2 y 4.");
            if (maxJugadores < Partida.MIN_JUGADORES || maxJugadores > Partida.MAX_JUGADORES) {
                throw new ErrorNegocioException("Cantidad de jugadores no válida", "Debe ser de mínimo 2 y máximo 4.");
            }

            int puerto = parsear(txtPuerto, "No se pudo crear la partida", "El puerto no es válido. Elige otro.");
            if (puerto < 1 || puerto > 65535) {
                throw new ErrorNegocioException("No se pudo crear la partida", "El puerto no es válido. Elige otro.");
            }

            Partida partida = Partida.crear(puerto, maxJugadores, red);
            sesion.setPartida(partida, true);
            nav.mostrarRegistroJugador();
        } catch (ValidacionException e) {
            vista.marcarCampoError(e.campo, e.getMessage());
        } catch (ErrorNegocioException e) {
            vista.mostrarModal(e.titulo, e.getMessage());
        }
    }

    private int parsear(String texto, String titulo, String mensaje) throws ErrorNegocioException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            throw new ErrorNegocioException(titulo, mensaje);
        }
    }
}
