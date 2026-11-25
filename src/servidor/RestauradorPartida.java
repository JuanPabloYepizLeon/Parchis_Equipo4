package servidor;

import static protocolo.Contrato.Snapshot.*;

import comun.ErrorNegocioException;
import java.util.List;
import java.util.Map;
import modelo.Color;
import modelo.EstadoJugador;
import modelo.EstadoPartida;
import modelo.FaseTurno;
import modelo.Ficha;
import modelo.Jugador;
import modelo.Partida;

/**
 * Vuelve a crear una partida a partir de una copia; se usa cuando cambia el anfitrión.
 */
final class RestauradorPartida {

    private RestauradorPartida() {
    }

    static Partida restaurar(Map<String, Object> snap) throws ErrorNegocioException {
        Partida partida = new Partida(entero(snap, MAX_JUGADORES, Partida.MAX_JUGADORES));
        if (snap.get(JUGADORES) instanceof List<?> lista) {
            for (Object o : lista) {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) o;
                Jugador j = new Jugador(texto(m, NOMBRE), Color.valueOf(texto(m, COLOR)), texto(m, AVATAR));
                j.setDireccion(texto(m, DIRECCION));
                EstadoJugador estado = EstadoJugador.valueOf(texto(m, ESTADO_JUGADOR));
                if (estado == EstadoJugador.ABANDONO) {
                    j.retirarFichas();
                } else if (m.get(FICHAS) instanceof List<?> fichas) {
                    for (Object fo : fichas) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> fm = (Map<String, Object>) fo;
                        Ficha f = j.getFicha(entero(fm, ID, 0));
                        if (f != null) {
                            f.colocarEn(entero(fm, POSICION, f.getPosicionRelativa()));
                        }
                    }
                }
                j.cambiarEstado(estado);
                partida.agregarJugador(j);
            }
        }
        partida.restaurar(new Partida.Instantanea(
                EstadoPartida.valueOf(texto(snap, ESTADO)),
                jugadorPorColor(partida, texto(snap, TURNO)),
                FaseTurno.valueOf(texto(snap, FASE)),
                entero(snap, PASOS_PENDIENTES, 0),
                entero(snap, SEIS_CONSECUTIVOS, 0),
                Boolean.TRUE.equals(snap.get(TIRO_EXTRA)),
                entero(snap, ULTIMO_DADO, 0),
                jugadorPorColor(partida, texto(snap, GANADOR)),
                texto(snap, MOTIVO_FIN),
                jugadorPorColor(partida, texto(snap, COLOR_ANFITRION))));
        return partida;
    }

    private static Jugador jugadorPorColor(Partida partida, String color) {
        return color == null ? null : partida.buscarJugador(Color.valueOf(color));
    }

    private static String texto(Map<String, Object> m, String clave) {
        Object o = m.get(clave);
        return o == null ? null : String.valueOf(o);
    }

    private static int entero(Map<String, Object> m, String clave, int porDefecto) {
        return m.get(clave) instanceof Number n ? n.intValue() : porDefecto;
    }
}
