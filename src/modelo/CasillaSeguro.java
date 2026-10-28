package modelo;

public class CasillaSeguro extends Casilla {

    public CasillaSeguro(int numero) {
        super(numero);
    }

    @Override
    public boolean esSegura() {
        return true;
    }
}
