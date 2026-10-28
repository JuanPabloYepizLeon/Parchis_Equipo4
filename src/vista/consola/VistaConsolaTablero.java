package vista.consola;

import java.util.List;
import modelo.Evento;
import modelo.Ficha;
import modelo.Jugador;
import modelo.Partida;
import modelo.PartidaObserver;
import modelo.ResultadoMovimiento;
import modelo.Tablero;
import vista.VistaTablero;

public class VistaConsolaTablero implements VistaTablero, PartidaObserver {

    private final String nombreVista;
    private final boolean detallada;

    public VistaConsolaTablero(String nombreVista, boolean detallada) {
        this.nombreVista = nombreVista;
        this.detallada = detallada;
    }

    @Override
    public void alCambiar(Evento evento, Partida p) {
        Tablero t = p.getTablero();
        Jugador quien = p.getUltimoJugador();
        ResultadoMovimiento m = p.getUltimoMovimiento();
        switch (evento) {
            case PARTIDA_INICIADA -> {
                if (detallada) {
                    linea("Partida iniciada. Empieza " + p.getJugadorEnTurno().getNombre()
                            + " (" + p.getJugadorEnTurno().getColor() + ")");
                    imprimirTablero(p);
                }
            }
            case DADO_LANZADO -> {
                if (detallada) {
                    linea(quien.getNombre() + " lanzó el dado: " + p.getUltimoValorDado()
                            + (p.getUltimoValorDado() == 6 ? "  (¡seis!)" : ""));
                }
            }
            case FICHA_MOVIDA -> {
                String origen = t.describirPosicion(m.ficha().getColor(), m.desde());
                String destino = t.describirPosicion(m.ficha().getColor(), m.hasta());
                linea(quien.getNombre() + " movió " + m.ficha().getEtiqueta() + ": " + origen + " -> " + destino
                        + (m.salioDeBase() ? "  (salió de la base)" : ""));
            }
            case FICHA_CAPTURADA -> {
                if (detallada) {
                    linea("¡Captura! " + m.capturadas() + " vuelve(n) a la base. "
                            + quien.getNombre() + " gana +" + m.bonificacion() + " de movimiento.");
                }
            }
            case FICHA_EN_META -> {
                if (detallada) {
                    linea(m.ficha().getEtiqueta() + " llegó a la META. " + quien.getNombre() + " gana +"
                            + m.bonificacion() + " de movimiento. Fichas en meta: " + quien.fichasEnMeta() + "/4");
                }
            }
            case TURNO_PERDIDO -> {
                if (detallada) {
                    linea(quien.getNombre() + " sacó el cuarto 6 seguido: pierde el turno sin mover.");
                }
            }
            case TURNO_CAMBIADO -> {
                linea("Turno de: " + p.getJugadorEnTurno().getNombre() + " (" + p.getJugadorEnTurno().getColor() + ")");
                if (detallada) {
                    imprimirTablero(p);
                }
            }
            case PARTIDA_TERMINADA -> {
                linea("*** PARTIDA TERMINADA. Ganador: " + p.getGanador().getNombre()
                        + " (4 fichas en meta) ***");
                if (detallada) {
                    imprimirTablero(p);
                }
            }
            default -> {
            }
        }
    }

    @Override
    public void mostrarFichasMovibles(List<Ficha> movibles, int pasos) {
        if (movibles.isEmpty()) {
            linea("Con un " + pasos + " no hay ninguna ficha que se pueda mover.");
        } else {
            linea("Con un " + pasos + " puedes mover: " + movibles);
        }
    }

    @Override
    public void mostrarError(String titulo, String mensaje) {
        linea("[!] " + titulo + ": " + mensaje);
    }

    public void imprimirTablero(Partida p) {
        Tablero t = p.getTablero();
        System.out.println("   -- TABLERO ----------------------------------------------------");
        for (Jugador j : p.getJugadores()) {
            StringBuilder sb = new StringBuilder();
            for (Ficha f : j.getFichas()) {
                sb.append(f.getEtiqueta()).append(": ")
                        .append(t.describirPosicion(f.getColor(), f.getPosicionRelativa())).append("  |  ");
            }
            System.out.println("   | " + j.getColor() + " (" + j.getNombre() + ", meta " + j.fichasEnMeta() + "/4)");
            System.out.println("   |    " + sb);
        }
        System.out.println("   | Fase: " + p.getFase()
                + (p.getPasosPendientes() > 0 ? " (valor pendiente " + p.getPasosPendientes() + ")" : ""));
        System.out.println("   ---------------------------------------------------------------");
    }

    private void linea(String texto) {
        System.out.println("[" + nombreVista + "] " + texto);
    }
}
