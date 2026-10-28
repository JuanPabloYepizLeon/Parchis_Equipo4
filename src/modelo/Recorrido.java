package modelo;

import java.util.ArrayList;
import java.util.List;

public class Recorrido {

    public static final int BASE = -1;
    public static final int ULTIMA_COMUN = 63;
    public static final int INICIO_PASILLO = 64;
    public static final int FIN_PASILLO = 70;
    public static final int META = 71;

    private final Color color;
    private final int salidaAbsoluta;
    private final List<Casilla> casillas = new ArrayList<>();

    public Recorrido(Color color, List<Casilla> casillasComunes) {
        this.color = color;
        this.salidaAbsoluta = salidaDe(color);
        for (int rel = 0; rel <= ULTIMA_COMUN; rel++) {
            casillas.add(casillasComunes.get(aAbsoluta(rel) - 1));
        }
        for (int i = 1; i <= FIN_PASILLO - INICIO_PASILLO + 1; i++) {
            casillas.add(new CasillaPasillo(i, color));
        }
        casillas.add(new CasillaMeta(color));
    }

    public static int salidaDe(Color color) {
        return switch (color) {
            case ROJO -> 5;
            case AZUL -> 22;
            case AMARILLO -> 39;
            case VERDE -> 56;
        };
    }

    public int aAbsoluta(int rel) {
        if (rel < 0 || rel > ULTIMA_COMUN) {
            throw new IllegalArgumentException("La posición " + rel + " no está en el recorrido común");
        }
        return ((salidaAbsoluta + rel - 1) % Tablero.TOTAL_CASILLAS) + 1;
    }

    public Casilla casillaEn(int rel) {
        return casillas.get(rel);
    }

    public int salidaAbsoluta() {
        return salidaAbsoluta;
    }

    public Color getColor() {
        return color;
    }
}
