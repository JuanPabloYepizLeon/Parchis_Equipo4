package servidor;

import comun.ErrorNegocioException;
import comun.Hospedaje;
import comun.ServidorHospedado;
import java.util.Map;
import servidor.servicios.ServicioJugador;
import servidor.servicios.ServicioPartida;
import servidor.servicios.ServicioSistema;
import servidor.servicios.ServicioTurno;

/**
 * Arma el servidor: registra todos los servicios y lo pone a escuchar en un puerto.
 */
public final class ParchisServidor implements Hospedaje {

    @Override
    public ServidorHospedado arrancar(int puerto) throws ErrorNegocioException {
        return arrancar(puerto, null, null);
    }

    @Override
    public ServidorHospedado arrancarRestaurando(int puerto, Map<String, Object> snapshot, String colorNuevoAnfitrion)
            throws ErrorNegocioException {
        return arrancar(puerto, snapshot, colorNuevoAnfitrion);
    }

    private static ServidorTcp arrancar(int puerto, Map<String, Object> snapshot, String colorNuevoAnfitrion)
            throws ErrorNegocioException {
        ContextoServidor ctx = new ContextoServidor();
        EnrutadorServicios enrutador = new EnrutadorServicios();
        enrutador.registrar(new ServicioSistema(enrutador))
                .registrar(new ServicioPartida(ctx))
                .registrar(new ServicioJugador(ctx))
                .registrar(new ServicioTurno(ctx));
        ServidorTcp servidor = ServidorTcp.iniciar(puerto, enrutador);
        ctx.vincular(servidor);
        if (snapshot != null) {
            try {
                ctx.restaurarPartida(snapshot, colorNuevoAnfitrion);
            } catch (ErrorNegocioException | RuntimeException e) {
                servidor.close();
                throw e;
            }
        }
        return servidor;
    }
}
