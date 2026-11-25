package modelo;

/**
 * Casilla del tablero. Puede ser normal o segura.
 */
public abstract class Casilla {

    private final int numero;

    protected Casilla(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }

    public abstract boolean esSegura();
}
