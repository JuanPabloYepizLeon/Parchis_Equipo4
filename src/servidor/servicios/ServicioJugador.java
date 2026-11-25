package servidor.servicios;

import comun.ErrorNegocioException;
import comun.ValidacionException;
import java.util.Map;
import modelo.Color;
import modelo.Jugador;
import modelo.Partida;
import protocolo.Contrato.Acciones;
import protocolo.Contrato.Campos;
import protocolo.Contrato.Servicios;
import servidor.ContextoServidor;
import servidor.ServicioBase;
import servidor.SnapshotPartida;

/**
 * Servicio "jugador": registrarse, marcarse como listo, abandonar y reanudar la partida.
 */
public final class ServicioJugador extends ServicioBase {

    public ServicioJugador(ContextoServidor ctx) {
        super(Servicios.JUGADOR);

        registrar(Acciones.REGISTRAR, (p, c) -> {
            String nombre = texto(p, Campos.NOMBRE);
            if (nombre == null || nombre.isBlank()) {
                throw new ValidacionException(Campos.NOMBRE, "El nombre no puede estar vacío");
            }
            if (texto(p, Campos.COLOR) == null) {
                throw new ValidacionException(Campos.COLOR, "Elige un color");
            }
            Color color = color(p);
            String avatar = texto(p, Campos.AVATAR);
            if (avatar == null) {
                throw new ValidacionException(Campos.AVATAR, "Elige un avatar");
            }

            Partida partida = ctx.partidaObligatoria();
            if (c.conexion().jugador() != null) {
                throw new ErrorNegocioException("Ya registrado", "Esta conexión ya tiene un jugador.");
            }
            Jugador j = new Jugador(nombre.trim(), color, avatar);
            j.setDireccion(c.conexion().direccionRemota());
            partida.agregarJugador(j);
            c.conexion().asignarJugador(j);
            if (c.conexion().esAnfitrion()) {
                partida.setAnfitrion(j);
            }
            Map<String, Object> m = SnapshotPartida.de(partida);
            m.put(Campos.MI_COLOR, color);
            return m;
        });

        registrar(Acciones.ABANDONAR, (p, c) -> {
            Jugador j = c.jugadorObligatorio();
            Partida partida = ctx.partidaObligatoria();
            partida.retirarJugador(j);
            c.conexion().asignarJugador(null);
            return SnapshotPartida.de(partida);
        });

        registrar(Acciones.REANUDAR, (p, c) -> {
            String nombre = texto(p, Campos.NOMBRE);
            if (nombre == null || texto(p, Campos.COLOR) == null) {
                throw new ValidacionException(Campos.COLOR, "Faltan datos para reanudar la partida");
            }
            Partida partida = ctx.partidaObligatoria();
            if (c.conexion().jugador() != null) {
                throw new ErrorNegocioException("Ya registrado", "Esta conexión ya tiene un jugador.");
            }
            Color color = color(p);
            Jugador j = partida.buscarJugador(color);
            if (j == null || !j.estaActivo() || !j.getNombre().equals(nombre)) {
                throw new ErrorNegocioException("No se pudo reanudar", "Ya no formas parte de la partida.");
            }
            j.setDireccion(c.conexion().direccionRemota());
            c.conexion().asignarJugador(j);
            boolean anfitrion = j == partida.getAnfitrion();
            if (anfitrion) {
                c.conexion().marcarAnfitrion();
            }
            Map<String, Object> m = SnapshotPartida.de(partida);
            m.put(Campos.MI_COLOR, color);
            m.put(Campos.ANFITRION, anfitrion);
            return m;
        });

        registrar(Acciones.LISTO, (p, c) -> {
            Jugador j = c.jugadorObligatorio();
            Partida partida = ctx.partidaObligatoria();
            partida.cambiarListo(j, !Boolean.FALSE.equals(p.get(Campos.LISTO)));
            return SnapshotPartida.de(partida);
        });
    }

    private static Color color(Map<String, Object> p) throws ValidacionException {
        try {
            return Color.valueOf(texto(p, Campos.COLOR));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidacionException(Campos.COLOR, "Color no válido");
        }
    }
}
