package modelo;

public class CasillaNormal extends Casilla {

    public CasillaNormal(int numero) {
        super(numero);
    }

    @Override
    public boolean esSegura() {
        return false;
    }
}
