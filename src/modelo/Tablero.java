package modelo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tablero {

    public static final int TOTAL_CASILLAS = 68;
    private static final Set<Integer> SEGUROS = Set.of(5, 12, 17, 22, 29, 34, 39, 46, 51, 56, 63, 68);

    private final List<Casilla> comunes = new ArrayList<>();
    private final Map<Color, Recorrido> recorridos = new EnumMap<>(Color.class);
    private final List<Ficha> fichas = new ArrayList<>();

    public Tablero() {
        for (int n = 1; n <= TOTAL_CASILLAS; n++) {
            comunes.add(crearCasilla(n));
        }
        for (Color c : Color.values()) {
            recorridos.put(c, new Recorrido(c, comunes));
        }
    }

    private Casilla crearCasilla(int n) {
        for (Color c : Color.values()) {
            if (Recorrido.salidaDe(c) == n) {
                return new CasillaSalida(n, c);
            }
        }
        return SEGUROS.contains(n) ? new CasillaSeguro(n) : new CasillaNormal(n);
    }

    public void registrarFichas(List<Ficha> nuevas) {
        fichas.addAll(nuevas);
    }

    public Casilla casilla(int absoluta) {
        return comunes.get(absoluta - 1);
    }

    public Recorrido recorrido(Color color) {
        return recorridos.get(color);
    }

    public int absolutaDe(Ficha f) {
        int rel = f.getPosicionRelativa();
        if (rel < 0 || rel > Recorrido.ULTIMA_COMUN) {
            return -1;
        }
        return recorridos.get(f.getColor()).aAbsoluta(rel);
    }

    public List<Ficha> fichasEnCasillaComun(int absoluta) {
        List<Ficha> resultado = new ArrayList<>();
        for (Ficha f : fichas) {
            if (absolutaDe(f) == absoluta) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    public boolean hayBarrera(int absoluta) {
        List<Ficha> aqui = fichasEnCasillaComun(absoluta);
        return aqui.size() == 2 && aqui.get(0).getColor() == aqui.get(1).getColor();
    }

    public List<Ficha> getFichas() {
        return List.copyOf(fichas);
    }

    public String describirPosicion(Color color, int rel) {
        if (rel == Recorrido.BASE) {
            return "base";
        }
        if (rel <= Recorrido.ULTIMA_COMUN) {
            int abs = recorridos.get(color).aAbsoluta(rel);
            return "casilla " + abs + (casilla(abs).esSegura() ? " (segura)" : "");
        }
        if (rel < Recorrido.META) {
            return "pasillo " + (rel - Recorrido.INICIO_PASILLO + 1) + "/7";
        }
        return "META";
    }
}
