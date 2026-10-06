package modelo;

import comun.ErrorNegocioException;
import comun.GestorRed;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Partida {

    public static final int MIN_JUGADORES = 2;
    public static final int MAX_JUGADORES = 4;
    private final int puerto;
    private final int maxJugadores;
    private EstadoPartida estado = EstadoPartida.EN_ESPERA;
    private final List<Jugador> jugadores = new ArrayList<>();
    private Jugador jugadorEnTurno;
    private final List<PartidaObserver> observadores = new CopyOnWriteArrayList<>();

    private Partida(int puerto, int maxJugadores) {
        this.puerto = puerto;
        this.maxJugadores = maxJugadores;
    }

    public static Partida crear(int puerto, int maxJugadores, GestorRed red) throws ErrorNegocioException {
        red.abrirServidor(puerto);
        return new Partida(puerto, maxJugadores);
    }

    public synchronized boolean solicitarAcceso() {
        return jugadores.size() < maxJugadores && estado == EstadoPartida.EN_ESPERA;
    }

    public synchronized Set<Color> coloresOcupados() {
        Set<Color> ocupados = EnumSet.noneOf(Color.class);
        for (Jugador j : jugadores) {
            ocupados.add(j.getColor());
        }
        return ocupados;
    }

    public synchronized void agregarJugador(Jugador j) throws ErrorNegocioException {
        if (coloresOcupados().contains(j.getColor())) {
            throw new ErrorNegocioException("Color ocupado", "Ese color ya fue tomado por otro jugador. Elige otro.");
        }
        jugadores.add(j);
        notificar(Evento.JUGADOR_REGISTRADO);
    }

    public synchronized boolean todosListos() {
        return jugadores.size() >= MIN_JUGADORES && jugadores.stream().allMatch(j -> j.getEstado() == EstadoJugador.LISTO);
    }

    public synchronized Jugador elegirJugadorInicial() {
        jugadorEnTurno = jugadores.get(new Random().nextInt(jugadores.size()));
        return jugadorEnTurno;
    }

    public synchronized void cambiarEstado(EstadoPartida estado) {
        this.estado = estado;
        if (estado == EstadoPartida.EN_JUEGO) {
            notificar(Evento.PARTIDA_INICIADA);
        }
    }

    public void jugadorCambioEstado() {
        notificar(Evento.ESTADO_JUGADOR_CAMBIADO);
    }

    public void agregarObservador(PartidaObserver o) {
        observadores.add(o);
    }

    public void quitarObservador(PartidaObserver o) {
        observadores.remove(o);
    }

    private void notificar(Evento e) {
        for (PartidaObserver o : observadores) {
            o.alCambiar(e, this);
        }
    }

    public List<Jugador> getJugadores() {
        return List.copyOf(jugadores);
    }

    public Jugador getJugadorEnTurno() {
        return jugadorEnTurno;
    }

    public int getPuerto() {
        return puerto;
    }

    public int getMaxJugadores() {
        return maxJugadores;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

}
