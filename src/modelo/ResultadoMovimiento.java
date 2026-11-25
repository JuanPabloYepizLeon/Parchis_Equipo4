package modelo;

import java.util.List;

/**
 * Resultado de mover una ficha: de dónde a dónde fue, qué capturó y si ganó bonificación.
 */
public record ResultadoMovimiento(
        Ficha ficha,
        int desde,
        int hasta,
        boolean salioDeBase,
        List<Ficha> capturadas,
        boolean llegoAMeta,
        int bonificacion) {
}
