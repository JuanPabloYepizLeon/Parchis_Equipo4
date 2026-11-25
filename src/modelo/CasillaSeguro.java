package modelo;

/**
 * Casilla segura: ahí no se pueden capturar fichas.
 */
public class CasillaSeguro extends Casilla {

    public CasillaSeguro(int numero) {
        super(numero);
    }

    @Override
    public boolean esSegura() {
        return true;
    }
}
