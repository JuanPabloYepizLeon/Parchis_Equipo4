package main;

import comun.GestorRed;
import comun.GestorRedLocal;
import comun.Sesion;
import controlador.EjercerTurnoController;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import modelo.Color;
import modelo.EstadoJugador;
import modelo.EstadoPartida;
import modelo.Ficha;
import modelo.FaseTurno;
import modelo.Jugador;
import modelo.Partida;
import vista.consola.VistaConsolaTablero;

public class MainConsola {

    private static final String[] NOMBRES = {"Ana", "Luis", "Marta", "Pedro"};
    private static final Color[] COLORES = Color.values();

    public static void main(String[] args) throws Exception {
        int n = 4;
        boolean auto = false;
        boolean vistas = false;
        for (String a : args) {
            if (a.equals("auto")) {
                auto = true;
            } else if (a.equals("vistas")) {
                vistas = true;
            } else {
                n = Math.max(2, Math.min(4, Integer.parseInt(a)));
            }
        }

        List<Sesion> sesiones = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            sesiones.add(new Sesion());
        }
        GestorRed red = new GestorRedLocal(sesiones.get(0));
        Partida partida = Partida.crear(5000, n, red);
        sesiones.get(0).setPartida(partida, true);

        List<EjercerTurnoController> controladores = new ArrayList<>();
        List<VistaConsolaTablero> vistasJugador = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Jugador j = new Jugador(NOMBRES[i], COLORES[i], "avatar" + (i + 1));
            partida.agregarJugador(j);
            j.cambiarEstado(EstadoJugador.LISTO);
            Sesion s = sesiones.get(i);
            s.setPartida(partida, i == 0);
            s.setJugadorLocal(j);
            VistaConsolaTablero v = new VistaConsolaTablero("Vista de " + NOMBRES[i], i == 0);
            vistasJugador.add(v);
            controladores.add(new EjercerTurnoController(v, s, red));
            if (i == 0 || vistas) {
                partida.agregarObservador(v);
            }
        }

        partida.elegirJugadorInicial();
        partida.cambiarEstado(EstadoPartida.EN_JUEGO);

        Scanner in = new Scanner(System.in);
        Random azar = new Random();
        int guardia = 0;
        while (partida.getEstado() == EstadoPartida.EN_JUEGO && guardia++ < 5000) {
            Jugador turno = partida.getJugadorEnTurno();
            EjercerTurnoController c = controladores.get(partida.getJugadores().indexOf(turno));

            if (partida.getFase() == FaseTurno.ESPERANDO_LANZAMIENTO) {
                if (auto) {
                    c.lanzarDado();
                } else {
                    System.out.print("\n" + turno.getNombre() + " [Enter = lanzar, 1-6 = forzar dado, q = salir] > ");
                    String linea = in.nextLine().trim();
                    if (linea.equalsIgnoreCase("q")) {
                        return;
                    }
                    if (linea.matches("[1-6]")) {
                        partida.getDado().forzarSiguiente(Integer.parseInt(linea));
                    }
                    c.lanzarDado();
                }
            } else {
                List<Ficha> movibles = partida.getFichasMovibles();
                if (auto) {
                    c.moverFicha(movibles.get(azar.nextInt(movibles.size())).getId());
                } else {
                    System.out.print(turno.getNombre() + " elige ficha (número) > ");
                    String linea = in.nextLine().trim();
                    if (linea.equalsIgnoreCase("q")) {
                        return;
                    }
                    try {
                        c.moverFicha(Integer.parseInt(linea));
                    } catch (NumberFormatException e) {
                        System.out.println("Escribe el número de la ficha (1-4).");
                    }
                }
            }
        }
    }
}
