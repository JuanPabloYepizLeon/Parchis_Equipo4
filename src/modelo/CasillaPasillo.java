package modelo;

public class CasillaPasillo extends Casilla {

    private final Color dueno;

    public CasillaPasillo(int indice, Color dueno) {
        super(indice);
        this.dueno = dueno;
    }

    public Color getDueno() {
        return dueno;
    }

    @Override
    public boolean esSegura() {
        return true;
    }
}
