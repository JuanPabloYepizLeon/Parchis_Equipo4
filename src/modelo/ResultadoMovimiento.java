package modelo;

import java.util.List;

public record ResultadoMovimiento(
        Ficha ficha,
        int desde,
        int hasta,
        boolean salioDeBase,
        List<Ficha> capturadas,
        boolean llegoAMeta,
        int bonificacion) {
}
