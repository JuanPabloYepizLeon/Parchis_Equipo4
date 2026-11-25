package cliente;

import java.util.List;
import java.util.Map;
import protocolo.Contrato.Campos;
import protocolo.Sobre;

/**
 * Resultado de lanzar el dado: valor obtenido, si se perdió el turno y si hay fichas para mover.
 */
public record ResultadoTiro(int valor, boolean pierdeTurno, boolean sinMovimientos) {

    static ResultadoTiro de(Sobre respuesta) {
        Map<String, Object> p = respuesta.payload();
        int valor = p.get(Campos.VALOR) instanceof Number n ? n.intValue() : 0;
        boolean sinMovimientos = !(p.get(Campos.MOVIBLES) instanceof List<?> l) || l.isEmpty();
        return new ResultadoTiro(valor, Boolean.TRUE.equals(p.get(Campos.PIERDE_TURNO)), sinMovimientos);
    }
}
