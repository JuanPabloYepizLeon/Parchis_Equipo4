package modelo;

import java.util.List;

/**
 * Resultado de lanzar el dado: quién tiró, qué salió y qué fichas puede mover.
 */
public record ResultadoLanzamiento(
        Jugador jugador,
        int valor,
        List<Ficha> movibles,
        boolean pierdeTurno) {
}
