package modelo;

import java.util.Random;

/**
 * Dado de seis caras.
 */
public class Dado {

    private final Random random = new Random();

    public int lanzar() {
        return random.nextInt(6) + 1;
    }
}
