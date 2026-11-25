package servidor.servicios;

import comun.ValidacionException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelo.Ficha;
import modelo.Jugador;
import modelo.ResultadoLanzamiento;
import modelo.ResultadoMovimiento;
import protocolo.Contrato.Acciones;
import protocolo.Contrato.Campos;
import protocolo.Contrato.Servicios;
import servidor.ContextoServidor;
import servidor.ServicioBase;
import servidor.SnapshotPartida;

/**
 * Servicio "turno": lanzar el dado y mover fichas.
 */
public final class ServicioTurno extends ServicioBase {

    public ServicioTurno(ContextoServidor ctx) {
        super(Servicios.TURNO);

        registrar(Acciones.LANZAR_DADO, (p, c) -> {
            Jugador j = c.jugadorObligatorio();
            ResultadoLanzamiento r = ctx.partidaObligatoria().lanzarDado(j);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put(Campos.VALOR, r.valor());
            m.put(Campos.PIERDE_TURNO, r.pierdeTurno());
            m.put(Campos.MOVIBLES, SnapshotPartida.ids(r.movibles()));
            return m;
        });

        registrar(Acciones.MOVER_FICHA, (p, c) -> {
            Jugador j = c.jugadorObligatorio();
            Integer id = entero(p, Campos.ID_FICHA);
            if (id == null) {
                throw new ValidacionException(Campos.ID_FICHA, "Indica la ficha a mover");
            }
            ResultadoMovimiento r = ctx.partidaObligatoria().moverFicha(j, id);
            List<Object> capturadas = new ArrayList<>();
            for (Ficha f : r.capturadas()) {
                capturadas.add(f.getEtiqueta());
            }
            Map<String, Object> m = new LinkedHashMap<>();
            m.put(Campos.DESDE, r.desde());
            m.put(Campos.HASTA, r.hasta());
            m.put(Campos.CAPTURADAS, capturadas);
            m.put(Campos.LLEGO_A_META, r.llegoAMeta());
            m.put(Campos.BONIFICACION, r.bonificacion());
            return m;
        });
    }
}
