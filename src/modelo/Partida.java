package modelo;

import comun.ErrorNegocioException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Partida de Parchís. Controla los jugadores, los turnos, el dado y el movimiento de las fichas,
 * y avisa a sus observadores cada vez que algo cambia.
 */
public class Partida {

    public static final int MIN_JUGADORES = 2;
    public static final int MAX_JUGADORES = 4;
    private final int maxJugadores;
    private EstadoPartida estado = EstadoPartida.EN_ESPERA;
    private final List<Jugador> jugadores = new ArrayList<>();
    private Jugador jugadorEnTurno;
    private final List<PartidaObserver> observadores = new CopyOnWriteArrayList<>();
    private final Tablero tablero = new Tablero();
    private final Dado dado = new Dado();
    private final ReglasJuego reglas = new ReglasEstandar();
    private FaseTurno fase = FaseTurno.ESPERANDO_LANZAMIENTO;
    private int pasosPendientes = 0;
    private int seisConsecutivos = 0;
    private boolean tiroExtra = false;
    private int ultimoValorDado = 0;
    private Jugador ultimoJugador;
    private Jugador ganador;
    private Jugador anfitrion;
    private String motivoFin;

    /** Datos del turno que se guardan para poder restaurar la partida. */
    public record Instantanea(EstadoPartida estado, Jugador turno, FaseTurno fase, int pasosPendientes,
            int seisConsecutivos, boolean tiroExtra, int ultimoDado, Jugador ganador, String motivoFin,
            Jugador anfitrion) {

    }

    public Partida(int maxJugadores) {
        this.maxJugadores = maxJugadores;
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
        if (estado != EstadoPartida.EN_ESPERA) {
            throw new ErrorNegocioException("Partida iniciada", "La partida ya comenzó; no se pueden registrar más jugadores.");
        }
        if (jugadores.size() >= maxJugadores) {
            throw new ErrorNegocioException("Partida llena", "Ya no hay lugares disponibles.");
        }
        if (coloresOcupados().contains(j.getColor())) {
            throw new ErrorNegocioException("Color ocupado", "Ese color ya fue tomado por otro jugador. Elige otro.");
        }
        jugadores.add(j);
        tablero.registrarFichas(j.getFichas());
        notificar(Evento.JUGADOR_REGISTRADO);
    }

    public synchronized boolean todosListos() {
        return jugadores.size() >= MIN_JUGADORES && jugadores.stream().allMatch(j -> j.getEstado() == EstadoJugador.LISTO);
    }

    public synchronized void cambiarListo(Jugador jugador, boolean listo) throws ErrorNegocioException {
        if (estado != EstadoPartida.EN_ESPERA) {
            throw new ErrorNegocioException("Acción no permitida", "La partida ya comenzó.");
        }
        jugador.cambiarEstado(listo ? EstadoJugador.LISTO : EstadoJugador.ESPERANDO);
        notificar(Evento.ESTADO_JUGADOR_CAMBIADO);
    }

    public synchronized void iniciar() throws ErrorNegocioException {
        if (estado != EstadoPartida.EN_ESPERA || !todosListos()) {
            throw new ErrorNegocioException("No se puede iniciar",
                    "Un jugador dejó de estar listo o se desconectó. Revisa el estado e inténtalo de nuevo.");
        }
        jugadorEnTurno = jugadores.get(new Random().nextInt(jugadores.size()));
        estado = EstadoPartida.EN_JUEGO;
        fase = FaseTurno.ESPERANDO_LANZAMIENTO;
        pasosPendientes = 0;
        seisConsecutivos = 0;
        tiroExtra = false;
        notificar(Evento.PARTIDA_INICIADA);
    }

    public synchronized ResultadoLanzamiento lanzarDado(Jugador jugador) throws ErrorNegocioException {
        validarTurno(jugador, FaseTurno.ESPERANDO_LANZAMIENTO);
        List<Evento> eventos = new ArrayList<>();

        int valor = dado.lanzar();
        ultimoValorDado = valor;
        ultimoJugador = jugador;
        eventos.add(Evento.DADO_LANZADO);

        if (valor == 6) {
            seisConsecutivos++;
            if (seisConsecutivos > reglas.maxSeisConsecutivos()) {
                avanzarTurno();
                eventos.add(Evento.TURNO_PERDIDO);
                eventos.add(Evento.TURNO_CAMBIADO);
                notificarTodos(eventos);
                return new ResultadoLanzamiento(jugador, valor, List.of(), true);
            }
            tiroExtra = true;
        }

        List<Ficha> movibles = reglas.fichasMovibles(jugador, valor, tablero);
        if (movibles.isEmpty()) {
            if (tiroExtra) {
                tiroExtra = false;
                fase = FaseTurno.ESPERANDO_LANZAMIENTO;
            } else {
                avanzarTurno();
                eventos.add(Evento.TURNO_CAMBIADO);
            }
        } else {
            pasosPendientes = valor;
            fase = FaseTurno.ESPERANDO_MOVIMIENTO;
        }
        notificarTodos(eventos);
        return new ResultadoLanzamiento(jugador, valor, movibles, false);
    }

    public synchronized ResultadoMovimiento moverFicha(Jugador jugador, int idFicha) throws ErrorNegocioException {
        validarTurno(jugador, FaseTurno.ESPERANDO_MOVIMIENTO);
        Ficha ficha = jugador.getFicha(idFicha);
        if (ficha == null) {
            throw new ErrorNegocioException("Ficha inexistente", "No tienes una ficha con el número " + idFicha + ".");
        }
        String motivo = reglas.motivoInvalido(ficha, pasosPendientes, tablero);
        if (motivo != null) {
            throw new ErrorNegocioException("Movimiento no válido", motivo);
        }

        List<Evento> eventos = new ArrayList<>();
        ResultadoMovimiento r = reglas.resolverMovimiento(ficha, pasosPendientes, tablero);
        ultimoJugador = jugador;
        eventos.add(Evento.FICHA_MOVIDA);
        if (!r.capturadas().isEmpty()) {
            eventos.add(Evento.FICHA_CAPTURADA);
        }
        if (r.llegoAMeta()) {
            eventos.add(Evento.FICHA_EN_META);
        }

        if (jugador.fichasEnMeta() == Jugador.FICHAS_POR_JUGADOR) {
            terminar(jugador, "META");
            eventos.add(Evento.PARTIDA_TERMINADA);
            notificarTodos(eventos);
            return r;
        }

        if (r.bonificacion() > 0 && !reglas.fichasMovibles(jugador, r.bonificacion(), tablero).isEmpty()) {
            pasosPendientes = r.bonificacion();
            fase = FaseTurno.ESPERANDO_MOVIMIENTO;
        } else if (tiroExtra) {
            tiroExtra = false;
            pasosPendientes = 0;
            fase = FaseTurno.ESPERANDO_LANZAMIENTO;
        } else {
            avanzarTurno();
            eventos.add(Evento.TURNO_CAMBIADO);
        }
        notificarTodos(eventos);
        return r;
    }

    private void validarTurno(Jugador jugador, FaseTurno faseEsperada) throws ErrorNegocioException {
        if (estado != EstadoPartida.EN_JUEGO) {
            throw new ErrorNegocioException("Partida no activa", "La partida no está en juego.");
        }
        if (jugador != jugadorEnTurno) {
            throw new ErrorNegocioException("No es tu turno", "Espera a que sea tu turno.");
        }
        if (fase != faseEsperada) {
            throw new ErrorNegocioException("Acción no permitida",
                    faseEsperada == FaseTurno.ESPERANDO_LANZAMIENTO
                    ? "Primero debes mover una ficha."
                    : "Primero debes lanzar el dado.");
        }
    }

    private void terminar(Jugador ganador, String motivo) {
        this.ganador = ganador;
        motivoFin = motivo;
        estado = EstadoPartida.TERMINADA;
        pasosPendientes = 0;
        tiroExtra = false;
        fase = FaseTurno.ESPERANDO_LANZAMIENTO;
    }

    private void avanzarTurno() {
        seisConsecutivos = 0;
        tiroExtra = false;
        pasosPendientes = 0;
        fase = FaseTurno.ESPERANDO_LANZAMIENTO;
        Color[] colores = Color.values();
        int actual = jugadorEnTurno.getColor().ordinal();
        for (int i = 1; i <= colores.length; i++) {
            Color candidato = colores[(actual + i) % colores.length];
            for (Jugador j : jugadores) {
                if (j.getColor() == candidato && j.estaActivo()) {
                    jugadorEnTurno = j;
                    return;
                }
            }
        }
    }

    public synchronized void retirarJugador(Jugador jugador) {
        if (jugador == null || !jugadores.contains(jugador) || !jugador.estaActivo()
                || estado == EstadoPartida.TERMINADA) {
            return;
        }
        boolean teniaElTurno = jugador == jugadorEnTurno;
        List<Evento> eventos = new ArrayList<>();
        ultimoJugador = jugador;
        tablero.retirarFichas(jugador.getFichas());
        jugador.retirarFichas();
        eventos.add(Evento.JUGADOR_ABANDONO);

        if (estado == EstadoPartida.EN_ESPERA) {
            jugadores.remove(jugador);
            if (anfitrion == jugador) {
                anfitrion = null;
            }
            notificarTodos(eventos);
            return;
        }

        jugador.cambiarEstado(EstadoJugador.ABANDONO);
        List<Jugador> activos = jugadoresActivos();
        if (activos.size() <= 1) {
            terminar(activos.isEmpty() ? null : activos.get(0), "ABANDONO");
            eventos.add(Evento.PARTIDA_TERMINADA);
        } else if (teniaElTurno) {
            avanzarTurno();
            eventos.add(Evento.TURNO_CAMBIADO);
        }
        notificarTodos(eventos);
    }

    private List<Jugador> jugadoresActivos() {
        List<Jugador> activos = new ArrayList<>();
        for (Jugador j : jugadores) {
            if (j.estaActivo()) {
                activos.add(j);
            }
        }
        return activos;
    }

    public synchronized Jugador buscarJugador(Color color) {
        for (Jugador j : jugadores) {
            if (j.getColor() == color) {
                return j;
            }
        }
        return null;
    }

    public synchronized void restaurar(Instantanea i) {
        estado = i.estado();
        jugadorEnTurno = i.turno();
        fase = i.fase();
        pasosPendientes = i.pasosPendientes();
        seisConsecutivos = i.seisConsecutivos();
        tiroExtra = i.tiroExtra();
        ultimoValorDado = i.ultimoDado();
        ganador = i.ganador();
        motivoFin = i.motivoFin();
        anfitrion = i.anfitrion();
    }

    public synchronized List<Ficha> getFichasMovibles() {
        if (estado != EstadoPartida.EN_JUEGO || fase != FaseTurno.ESPERANDO_MOVIMIENTO) {
            return List.of();
        }
        return reglas.fichasMovibles(jugadorEnTurno, pasosPendientes, tablero);
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

    private void notificarTodos(List<Evento> eventos) {
        for (Evento e : eventos) {
            notificar(e);
        }
    }

    public synchronized List<Jugador> getJugadores() {
        return List.copyOf(jugadores);
    }

    public synchronized Jugador getJugadorEnTurno() {
        return jugadorEnTurno;
    }

    public int getMaxJugadores() {
        return maxJugadores;
    }

    public synchronized EstadoPartida getEstado() {
        return estado;
    }

    public synchronized FaseTurno getFase() {
        return fase;
    }

    public synchronized int getPasosPendientes() {
        return pasosPendientes;
    }

    public synchronized int getUltimoValorDado() {
        return ultimoValorDado;
    }

    public synchronized Jugador getUltimoJugador() {
        return ultimoJugador;
    }

    public synchronized Jugador getGanador() {
        return ganador;
    }

    public synchronized String getMotivoFin() {
        return motivoFin;
    }

    public synchronized Jugador getAnfitrion() {
        return anfitrion;
    }

    public synchronized void setAnfitrion(Jugador anfitrion) {
        this.anfitrion = anfitrion;
    }

    public synchronized int getSeisConsecutivos() {
        return seisConsecutivos;
    }

    public synchronized boolean isTiroExtra() {
        return tiroExtra;
    }

}
