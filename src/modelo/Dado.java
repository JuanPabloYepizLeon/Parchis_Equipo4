package modelo;

import java.util.Random;

public class Dado {

    private final Random random;
    private int forzado = 0;

    public Dado() {
        this.random = new Random();
    }

    public Dado(long semilla) {
        this.random = new Random(semilla);
    }

    public int lanzar() {
        if (forzado != 0) {
            int valor = forzado;
            forzado = 0;
            return valor;
        }
        return random.nextInt(6) + 1;
    }

    public void forzarSiguiente(int valor) {
        if (valor < 1 || valor > 6) {
            throw new IllegalArgumentException("El dado solo da valores de 1 a 6");
        }
        this.forzado = valor;
    }
}
