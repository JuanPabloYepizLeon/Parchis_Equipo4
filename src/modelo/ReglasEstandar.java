package modelo;

import java.util.ArrayList;
import java.util.List;

public class ReglasEstandar implements ServicioReglas {

    public static final int PASOS_PARA_SALIR = 5;
    public static final int MAX_SEIS_CONSECUTIVOS = 3;
    public static final int BONO_CAPTURA = 20;
    public static final int BONO_META = 10;

    private final boolean salidaObligatoria;

    public ReglasEstandar() {
        this(false);
    }

    public ReglasEstandar(boolean salidaObligatoria) {
        this.salidaObligatoria = salidaObligatoria;
    }

    @Override
    public String motivoInvalido(Ficha f, int pasos, Tablero t) {
        return switch (f.getEstado()) {
            case EN_META -> "Esa ficha ya está en la meta.";
            case EN_BASE -> motivoSalida(f, pasos, t);
            default -> motivoAvance(f, pasos, t);
        };
    }

    private String motivoSalida(Ficha f, int pasos, Tablero t) {
        if (pasos != PASOS_PARA_SALIR) {
            return "Necesitas un 5 para sacar una ficha de la base.";
        }
        int salida = t.recorrido(f.getColor()).salidaAbsoluta();
        List<Ficha> aqui = t.fichasEnCasillaComun(salida);
        if (t.hayBarrera(salida) && aqui.get(0).getColor() != f.getColor()) {
            return "Hay una barrera rival en tu casilla de salida.";
        }
        if (contarPropias(aqui, f) >= 2) {
            return "Ya tienes dos fichas en tu casilla de salida.";
        }
        return null;
    }

    private String motivoAvance(Ficha f, int pasos, Tablero t) {
        int rel = f.getPosicionRelativa();
        int nuevo = rel + pasos;
        if (nuevo > Recorrido.META) {
            return "Te pasarías de la meta: necesitas el número exacto.";
        }
        if (rel <= Recorrido.ULTIMA_COMUN) {
            Recorrido rec = t.recorrido(f.getColor());
            int ultimaComun = Math.min(nuevo, Recorrido.ULTIMA_COMUN);
            for (int r = rel + 1; r <= ultimaComun; r++) {
                int abs = rec.aAbsoluta(r);
                if (r < nuevo && t.hayBarrera(abs)) {
                    return "Hay una barrera en el camino (casilla " + abs + ").";
                }
                if (r == nuevo) {
                    return motivoAterrizaje(f, abs, t);
                }
            }
        }
        return null;
    }

    private String motivoAterrizaje(Ficha f, int abs, Tablero t) {
        List<Ficha> aqui = t.fichasEnCasillaComun(abs);
        int propias = contarPropias(aqui, f);
        int rivales = aqui.size() - propias;
        if (propias >= 2) {
            return "Esa casilla ya tiene dos fichas tuyas.";
        }
        if (t.hayBarrera(abs)) {
            return "La casilla " + abs + " está bloqueada por una barrera rival.";
        }
        if (t.casilla(abs).esSegura() && aqui.size() >= 2) {
            return "La casilla segura " + abs + " ya está llena.";
        }
        if (!t.casilla(abs).esSegura() && rivales > 1) {
            return "La casilla " + abs + " está ocupada.";
        }
        return null;
    }

    private int contarPropias(List<Ficha> fichas, Ficha f) {
        int n = 0;
        for (Ficha otra : fichas) {
            if (otra.getColor() == f.getColor()) {
                n++;
            }
        }
        return n;
    }

    @Override
    public List<Ficha> fichasMovibles(Jugador jugador, int pasos, Tablero t) {
        List<Ficha> movibles = new ArrayList<>();
        for (Ficha f : jugador.getFichas()) {
            if (esMovimientoValido(f, pasos, t)) {
                movibles.add(f);
            }
        }
        if (salidaObligatoria && pasos == PASOS_PARA_SALIR) {
            List<Ficha> salidas = movibles.stream().filter(f -> f.getEstado() == EstadoFicha.EN_BASE).toList();
            if (!salidas.isEmpty()) {
                return new ArrayList<>(salidas);
            }
        }
        return movibles;
    }

    @Override
    public ResultadoMovimiento resolverMovimiento(Ficha f, int pasos, Tablero t) {
        int desde = f.getPosicionRelativa();
        boolean salio = f.getEstado() == EstadoFicha.EN_BASE;
        int hasta = salio ? 0 : desde + pasos;
        List<Ficha> capturadas = new ArrayList<>();

        f.colocarEn(hasta);
        if (hasta <= Recorrido.ULTIMA_COMUN) {
            int abs = t.absolutaDe(f);
            boolean puedeCapturar = salio || !t.casilla(abs).esSegura();
            if (puedeCapturar) {
                for (Ficha otra : t.fichasEnCasillaComun(abs)) {
                    if (otra.getColor() != f.getColor()) {
                        otra.regresarABase();
                        capturadas.add(otra);
                    }
                }
            }
        }
        boolean meta = hasta == Recorrido.META;
        int bono = !capturadas.isEmpty() ? BONO_CAPTURA : (meta ? BONO_META : 0);
        return new ResultadoMovimiento(f, desde, hasta, salio, List.copyOf(capturadas), meta, bono);
    }

    @Override
    public int maxSeisConsecutivos() {
        return MAX_SEIS_CONSECUTIVOS;
    }
}
