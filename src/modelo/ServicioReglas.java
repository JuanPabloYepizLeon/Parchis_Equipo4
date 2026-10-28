package modelo;

import java.util.List;

public interface ServicioReglas {

    String motivoInvalido(Ficha ficha, int pasos, Tablero tablero);

    default boolean esMovimientoValido(Ficha ficha, int pasos, Tablero tablero) {
        return motivoInvalido(ficha, pasos, tablero) == null;
    }

    List<Ficha> fichasMovibles(Jugador jugador, int pasos, Tablero tablero);

    ResultadoMovimiento resolverMovimiento(Ficha ficha, int pasos, Tablero tablero);

    int maxSeisConsecutivos();
}
