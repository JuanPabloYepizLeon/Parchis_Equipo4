package modelo;

/**
 * Camino que sigue una ficha de cierto color: desde su salida hasta la meta.
 */
public class Recorrido {

    public static final int BASE = -1;
    public static final int ULTIMA_COMUN = 63;
    public static final int INICIO_PASILLO = 64;
    public static final int FIN_PASILLO = 70;
    public static final int META = 71;

    private final Color color;
    private final int salidaAbsoluta;

    public Recorrido(Color color) {
        this.color = color;
        this.salidaAbsoluta = salidaDe(color);
    }

    public static int salidaDe(Color color) {
        return switch (color) {
            case ROJO -> 39;
            case AZUL -> 22;
            case AMARILLO -> 5;
            case VERDE -> 56;
        };
    }

    /** Convierte una posición relativa del recorrido común de {@code color} en su casilla absoluta (1-68). */
    public static int aAbsoluta(Color color, int rel) {
        if (rel < 0 || rel > ULTIMA_COMUN) {
            throw new IllegalArgumentException("La posición " + rel + " no está en el recorrido común");
        }
        return ((salidaDe(color) + rel - 1) % Tablero.TOTAL_CASILLAS) + 1;
    }

    public int aAbsoluta(int rel) {
        return aAbsoluta(color, rel);
    }

    public int salidaAbsoluta() {
        return salidaAbsoluta;
    }

    public Color getColor() {
        return color;
    }
}
