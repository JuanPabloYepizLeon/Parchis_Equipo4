package modelo;

public class CasillaMeta extends Casilla {

    private final Color dueno;

    public CasillaMeta(Color dueno) {
        super(0);
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
