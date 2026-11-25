package servidor;

import static protocolo.Contrato.Snapshot.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelo.Ficha;
import modelo.Jugador;
import modelo.Partida;

/**
 * Convierte la partida en una copia que se puede enviar a los jugadores.
 */
public final class SnapshotPartida {

    private SnapshotPartida() {
    }

    public static Map<String, Object> de(Partida p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(ESTADO, p.getEstado());
        m.put(MAX_JUGADORES, p.getMaxJugadores());
        m.put(TODOS_LISTOS, p.todosListos());
        m.put(TURNO, colorDe(p.getJugadorEnTurno()));
        m.put(FASE, p.getFase());
        m.put(PASOS_PENDIENTES, p.getPasosPendientes());
        m.put(ULTIMO_DADO, p.getUltimoValorDado());
        m.put(GANADOR, colorDe(p.getGanador()));
        m.put(MOTIVO_FIN, p.getMotivoFin());
        m.put(COLOR_ANFITRION, colorDe(p.getAnfitrion()));
        m.put(SEIS_CONSECUTIVOS, p.getSeisConsecutivos());
        m.put(TIRO_EXTRA, p.isTiroExtra());
        m.put(ULTIMO_JUGADOR, colorDe(p.getUltimoJugador()));
        m.put(MOVIBLES, ids(p.getFichasMovibles()));
        List<Object> js = new ArrayList<>();
        for (Jugador j : p.getJugadores()) {
            Map<String, Object> jm = new LinkedHashMap<>();
            jm.put(NOMBRE, j.getNombre());
            jm.put(COLOR, j.getColor());
            jm.put(AVATAR, j.getAvatar());
            jm.put(ESTADO_JUGADOR, j.getEstado());
            jm.put(DIRECCION, j.getDireccion());
            jm.put(FICHAS_EN_META, j.fichasEnMeta());
            List<Object> fs = new ArrayList<>();
            for (Ficha f : j.getFichas()) {
                Map<String, Object> fm = new LinkedHashMap<>();
                fm.put(ID, f.getId());
                fm.put(ESTADO_FICHA, f.getEstado());
                fm.put(POSICION, f.getPosicionRelativa());
                fs.add(fm);
            }
            jm.put(FICHAS, fs);
            js.add(jm);
        }
        m.put(JUGADORES, js);
        return m;
    }

    public static List<Object> ids(List<Ficha> fichas) {
        List<Object> ids = new ArrayList<>();
        for (Ficha f : fichas) {
            ids.add(f.getId());
        }
        return ids;
    }

    private static Object colorDe(Jugador j) {
        return j == null ? null : j.getColor();
    }
}
