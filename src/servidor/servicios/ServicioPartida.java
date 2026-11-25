package servidor.servicios;

import comun.ErrorNegocioException;
import comun.ValidacionException;
import java.util.Map;
import modelo.Partida;
import protocolo.Contrato.Acciones;
import protocolo.Contrato.Campos;
import protocolo.Contrato.Servicios;
import servidor.ContextoServidor;
import servidor.ServicioBase;
import servidor.SnapshotPartida;

/**
 * Servicio "partida": crear la partida, unirse, iniciarla y consultar su estado.
 */
public final class ServicioPartida extends ServicioBase {

    public ServicioPartida(ContextoServidor ctx) {
        super(Servicios.PARTIDA);

        registrar(Acciones.CREAR, (p, c) -> {
            Integer max = entero(p, Campos.MAX_JUGADORES);
            if (max == null) {
                throw new ValidacionException(Campos.MAX_JUGADORES, "La cantidad de jugadores no puede estar vacía");
            }
            if (max < Partida.MIN_JUGADORES || max > Partida.MAX_JUGADORES) {
                throw new ErrorNegocioException("Cantidad de jugadores no válida", "Debe ser de mínimo 2 y máximo 4.");
            }
            Partida partida = ctx.crearPartida(max);
            c.conexion().marcarAnfitrion();
            return respuesta(partida, true);
        });

        registrar(Acciones.UNIRSE, (p, c) -> {
            Partida partida;
            try {
                partida = ctx.partidaObligatoria();
            } catch (ErrorNegocioException e) {
                throw new ErrorNegocioException("No se pudo unirse", "Sin respuesta o partida llena.");
            }
            if (!partida.solicitarAcceso()) {
                throw new ErrorNegocioException("No se pudo unirse", "Sin respuesta o partida llena.");
            }
            return respuesta(partida, false);
        });

        registrar(Acciones.INICIAR, (p, c) -> {
            Partida partida = ctx.partidaObligatoria();
            if (!c.conexion().esAnfitrion()) {
                throw new ErrorNegocioException("No permitido", "Solo el anfitrión puede iniciar la partida.");
            }
            partida.iniciar();
            return respuesta(partida, true);
        });

        registrar(Acciones.ESTADO, (p, c) -> respuesta(ctx.partidaObligatoria(), c.conexion().esAnfitrion()));
    }

    private static Map<String, Object> respuesta(Partida partida, boolean anfitrion) {
        Map<String, Object> m = SnapshotPartida.de(partida);
        m.put(Campos.ANFITRION, anfitrion);
        return m;
    }
}
