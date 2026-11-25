package vista.swing;

import java.awt.geom.Point2D;
import java.util.EnumMap;
import java.util.Map;
import modelo.Color;
import modelo.Recorrido;
import modelo.Tablero;

/**
 * Calcula en qué punto de la imagen del tablero se dibuja cada casilla y cada ficha.
 */
final class CoordenadasTablero {

    static final double TAM = 600;

    private static final double PROF = 27;
    private static final double CARRIL_IZQ = 190 + 220 / 6.0;
    private static final double CENTRO = TAM / 2;
    private static final double CARRIL_DER = 410 - 220 / 6.0;
    private static final double INICIO_BRAZO = TAM - 8 * PROF;
    private static final double SEPARACION_BASE = 30;
    private static final double SEPARACION_META = 9;

    private static final Point2D.Double[] COMUNES = new Point2D.Double[Tablero.TOTAL_CASILLAS + 1];
    private static final Map<Color, Point2D.Double[]> PASILLOS = new EnumMap<>(Color.class);
    private static final Map<Color, Point2D.Double> CENTRO_BASE = new EnumMap<>(Color.class);
    private static final Map<Color, Point2D.Double> CENTRO_META = new EnumMap<>(Color.class);

    static {
        for (Color c : Color.values()) {
            PASILLOS.put(c, new Point2D.Double[8]);
        }

        for (int r = 0; r < 8; r++) {
            double y = INICIO_BRAZO + d(r);
            COMUNES[8 - r] = pt(CARRIL_DER, y);
            COMUNES[60 + r] = pt(CARRIL_IZQ, y);
            double x = INICIO_BRAZO + d(r);
            COMUNES[9 + r] = pt(x, CARRIL_DER);
            COMUNES[25 - r] = pt(x, CARRIL_IZQ);
            COMUNES[33 - r] = pt(CARRIL_DER, d(r));
            COMUNES[35 + r] = pt(CARRIL_IZQ, d(r));
            COMUNES[50 - r] = pt(d(r), CARRIL_IZQ);
            COMUNES[52 + r] = pt(d(r), CARRIL_DER);
        }
        COMUNES[68] = pt(CENTRO, INICIO_BRAZO + d(7));
        COMUNES[17] = pt(INICIO_BRAZO + d(7), CENTRO);
        COMUNES[34] = pt(CENTRO, d(0));
        COMUNES[51] = pt(d(0), CENTRO);

        for (int i = 1; i <= 7; i++) {
            PASILLOS.get(Color.AMARILLO)[i] = pt(CENTRO, INICIO_BRAZO + d(7 - i));
            PASILLOS.get(Color.AZUL)[i] = pt(INICIO_BRAZO + d(7 - i), CENTRO);
            PASILLOS.get(Color.ROJO)[i] = pt(CENTRO, d(i));
            PASILLOS.get(Color.VERDE)[i] = pt(d(i), CENTRO);
        }

        CENTRO_BASE.put(Color.ROJO, pt(95, 95));
        CENTRO_BASE.put(Color.AZUL, pt(505, 95));
        CENTRO_BASE.put(Color.AMARILLO, pt(505, 505));
        CENTRO_BASE.put(Color.VERDE, pt(95, 505));

        CENTRO_META.put(Color.ROJO, pt(CENTRO, 250));
        CENTRO_META.put(Color.AZUL, pt(350, CENTRO));
        CENTRO_META.put(Color.AMARILLO, pt(CENTRO, 350));
        CENTRO_META.put(Color.VERDE, pt(250, CENTRO));
    }

    private CoordenadasTablero() {
    }

    static Point2D.Double casilla(int absoluta) {
        return copia(COMUNES[absoluta]);
    }

    static Point2D.Double de(Color color, int idFicha, int rel) {
        if (rel == Recorrido.BASE) {
            return puesto(CENTRO_BASE.get(color), idFicha, SEPARACION_BASE);
        }
        if (rel <= Recorrido.ULTIMA_COMUN) {
            return copia(COMUNES[Recorrido.aAbsoluta(color, rel)]);
        }
        if (rel <= Recorrido.FIN_PASILLO) {
            return copia(PASILLOS.get(color)[rel - Recorrido.ULTIMA_COMUN]);
        }
        return puesto(CENTRO_META.get(color), idFicha, SEPARACION_META);
    }

    private static Point2D.Double puesto(Point2D.Double centro, int idFicha, double separacion) {
        double dx = ((idFicha - 1) % 2 == 0 ? -1 : 1) * separacion;
        double dy = ((idFicha - 1) / 2 == 0 ? -1 : 1) * separacion;
        return pt(centro.x + dx, centro.y + dy);
    }

    private static double d(int i) {
        return PROF * (i + 0.5);
    }

    private static Point2D.Double pt(double x, double y) {
        return new Point2D.Double(x, y);
    }

    private static Point2D.Double copia(Point2D.Double p) {
        return pt(p.x, p.y);
    }
}
