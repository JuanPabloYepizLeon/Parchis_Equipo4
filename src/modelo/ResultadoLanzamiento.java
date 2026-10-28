package modelo;

import java.util.List;

public record ResultadoLanzamiento(
        Jugador jugador,
        int valor,
        List<Ficha> movibles,
        boolean pierdeTurno) {
}
