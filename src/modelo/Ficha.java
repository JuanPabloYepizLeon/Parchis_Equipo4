package modelo;

public class Ficha {

    private final int id;
    private final Color color;
    private int posicionRelativa = Recorrido.BASE;

    public Ficha(int id, Color color) {
        this.id = id;
        this.color = color;
    }

    public EstadoFicha getEstado() {
        if (posicionRelativa == Recorrido.BASE) {
            return EstadoFicha.EN_BASE;
        }
        if (posicionRelativa <= Recorrido.ULTIMA_COMUN) {
            return EstadoFicha.EN_RECORRIDO;
        }
        if (posicionRelativa < Recorrido.META) {
            return EstadoFicha.EN_PASILLO;
        }
        return EstadoFicha.EN_META;
    }

    public void salirDeBase() {
        posicionRelativa = 0;
    }

    public void avanzar(int pasos) {
        posicionRelativa += pasos;
    }

    public void regresarABase() {
        posicionRelativa = Recorrido.BASE;
    }

    public void colocarEn(int posicionRelativa) {
        this.posicionRelativa = posicionRelativa;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public int getPosicionRelativa() {
        return posicionRelativa;
    }

    public String getEtiqueta() {
        return color.name().substring(0, 3) + id;
    }

    @Override
    public String toString() {
        return getEtiqueta();
    }
}
