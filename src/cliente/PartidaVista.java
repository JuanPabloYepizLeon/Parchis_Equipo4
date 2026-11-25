package cliente;

import static protocolo.Contrato.Snapshot.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import modelo.Color;
import modelo.EstadoJugador;
import modelo.EstadoPartida;
import modelo.FaseTurno;

/**
 * Copia del estado de la partida que el cliente recibe del servidor.
 * Es lo que usan los controladores y las vistas para mostrar el juego.
 */
public record PartidaVista(
        EstadoPartida estado,
        int maxJugadores,
        boolean todosListos,
        Color turno,
        FaseTurno fase,
        int pasosPendientes,
        int ultimoDado,
        Color ganador,
        String motivoFin,
        Color anfitrion,
        Color ultimoJugador,
        List<Integer> movibles,
        List<JugadorVista> jugadores) {

    public static final PartidaVista VACIA = new PartidaVista(null, 0, false, null, null, 0, 0, null, null, null,
            null, List.of(), List.of());

    /** Datos de un jugador en la copia de la partida. */
    public record JugadorVista(String nombre, Color color, String avatar, EstadoJugador estado, String direccion,
            int fichasEnMeta, List<FichaVista> fichas) {

        public boolean abandono() {
            return estado == EstadoJugador.ABANDONO;
        }
    }

    /** Número de una ficha y su posición. */
    public record FichaVista(int id, int posicion) {

    }

    public static PartidaVista de(Map<String, Object> m) {
        List<JugadorVista> jugadores = new ArrayList<>();
        for (Map<String, Object> j : mapas(m.get(JUGADORES))) {
            List<FichaVista> fichas = new ArrayList<>();
            for (Map<String, Object> f : mapas(j.get(FICHAS))) {
                fichas.add(new FichaVista(entero(f, ID), entero(f, POSICION)));
            }
            jugadores.add(new JugadorVista(texto(j, NOMBRE), enumDe(Color.class, j, COLOR),
                    texto(j, AVATAR), enumDe(EstadoJugador.class, j, ESTADO_JUGADOR), texto(j, DIRECCION),
                    entero(j, FICHAS_EN_META), List.copyOf(fichas)));
        }
        return new PartidaVista(
                enumDe(EstadoPartida.class, m, ESTADO),
                entero(m, MAX_JUGADORES),
                Boolean.TRUE.equals(m.get(TODOS_LISTOS)),
                enumDe(Color.class, m, TURNO),
                enumDe(FaseTurno.class, m, FASE),
                entero(m, PASOS_PENDIENTES),
                entero(m, ULTIMO_DADO),
                enumDe(Color.class, m, GANADOR),
                texto(m, MOTIVO_FIN),
                enumDe(Color.class, m, COLOR_ANFITRION),
                enumDe(Color.class, m, ULTIMO_JUGADOR),
                enteros(m.get(MOVIBLES)),
                List.copyOf(jugadores));
    }

    public JugadorVista jugador(Color color) {
        for (JugadorVista j : jugadores) {
            if (j.color() == color) {
                return j;
            }
        }
        return null;
    }

    public String nombreDe(Color color) {
        JugadorVista j = jugador(color);
        return j == null ? "?" : j.nombre();
    }

    public Set<Color> coloresOcupados() {
        Set<Color> r = EnumSet.noneOf(Color.class);
        for (JugadorVista j : jugadores) {
            r.add(j.color());
        }
        return r;
    }

    /** Jugadores activos que podrían asumir como anfitrión si el actual se desconecta, en orden. */
    public List<JugadorVista> candidatosAnfitrion() {
        List<JugadorVista> r = new ArrayList<>();
        for (JugadorVista j : jugadores) {
            if (!j.abandono() && j.color() != anfitrion) {
                r.add(j);
            }
        }
        return r;
    }

    public boolean esMiTurnoDeMover(Color miColor) {
        return estado == EstadoPartida.EN_JUEGO && fase == FaseTurno.ESPERANDO_MOVIMIENTO
                && miColor != null && miColor == turno && !movibles.isEmpty();
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> mapas(Object o) {
        List<Map<String, Object>> r = new ArrayList<>();
        if (o instanceof List<?> l) {
            for (Object e : l) {
                if (e instanceof Map<?, ?>) {
                    r.add((Map<String, Object>) e);
                }
            }
        }
        return r;
    }

    private static List<Integer> enteros(Object o) {
        List<Integer> r = new ArrayList<>();
        if (o instanceof List<?> l) {
            for (Object e : l) {
                if (e instanceof Number n) {
                    r.add(n.intValue());
                }
            }
        }
        return List.copyOf(r);
    }

    private static String texto(Map<String, Object> m, String clave) {
        Object o = m.get(clave);
        return o == null ? null : String.valueOf(o);
    }

    private static int entero(Map<String, Object> m, String clave) {
        return m.get(clave) instanceof Number n ? n.intValue() : 0;
    }

    private static <E extends Enum<E>> E enumDe(Class<E> tipo, Map<String, Object> m, String clave) {
        String t = texto(m, clave);
        return t == null ? null : Enum.valueOf(tipo, t);
    }
}
