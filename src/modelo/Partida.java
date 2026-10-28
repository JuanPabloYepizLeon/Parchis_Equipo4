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
    private final Tablero tablero = new Tablero();
    private Dado dado = new Dado();
    private ServicioReglas reglas = new ReglasEstandar();
    private FaseTurno fase = FaseTurno.ESPERANDO_LANZAMIENTO;
    private int pasosPendientes = 0;
    private int seisConsecutivos = 0;
    private boolean tiroExtra = false;
    private int ultimoValorDado = 0;
    private Jugador ultimoJugador;
    private ResultadoMovimiento ultimoMovimiento;
    private Jugador ganador;

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
        tablero.registrarFichas(j.getFichas());
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
            fase = FaseTurno.ESPERANDO_LANZAMIENTO;
            pasosPendientes = 0;
            seisConsecutivos = 0;
            tiroExtra = false;
            notificar(Evento.PARTIDA_INICIADA);
        }
    }

    public void jugadorCambioEstado() {
        notificar(Evento.ESTADO_JUGADOR_CAMBIADO);
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
        ultimoMovimiento = r;
        ultimoJugador = jugador;
        eventos.add(Evento.FICHA_MOVIDA);
        if (!r.capturadas().isEmpty()) {
            eventos.add(Evento.FICHA_CAPTURADA);
        }
        if (r.llegoAMeta()) {
            eventos.add(Evento.FICHA_EN_META);
        }

        if (jugador.fichasEnMeta() == Jugador.FICHAS_POR_JUGADOR) {
            ganador = jugador;
            estado = EstadoPartida.TERMINADA;
            pasosPendientes = 0;
            tiroExtra = false;
            fase = FaseTurno.ESPERANDO_LANZAMIENTO;
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
                if (j.getColor() == candidato) {
                    jugadorEnTurno = j;
                    return;
                }
            }
        }
    }

    public synchronized List<Ficha> getFichasMovibles() {
        if (fase != FaseTurno.ESPERANDO_MOVIMIENTO) {
            return List.of();
        }
        return reglas.fichasMovibles(jugadorEnTurno, pasosPendientes, tablero);
    }

    public void setDado(Dado dado) {
        this.dado = dado;
    }

    public void setReglas(ServicioReglas reglas) {
        this.reglas = reglas;
    }

    public Dado getDado() {
        return dado;
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

    public Tablero getTablero() {
        return tablero;
    }

    public FaseTurno getFase() {
        return fase;
    }

    public int getPasosPendientes() {
        return pasosPendientes;
    }

    public int getUltimoValorDado() {
        return ultimoValorDado;
    }

    public Jugador getUltimoJugador() {
        return ultimoJugador;
    }

    public ResultadoMovimiento getUltimoMovimiento() {
        return ultimoMovimiento;
    }

    public Jugador getGanador() {
        return ganador;
    }

}
