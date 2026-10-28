package modelo;

public class CasillaSalida extends CasillaSeguro {

    private final Color dueno;

    public CasillaSalida(int numero, Color dueno) {
        super(numero);
        this.dueno = dueno;
    }

    public Color getDueno() {
        return dueno;
    }
}
