package cliente;

import comun.ErrorNegocioException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import protocolo.Contrato.Acciones;
import protocolo.Contrato.Campos;
import protocolo.Contrato.Servicios;
import protocolo.Sobre;

/**
 * Representa al servidor dentro del cliente: cada método pide una acción al servidor
 * (crear partida, registrarse, lanzar el dado, etc.).
 */
public final class ParchisRemoto {

    /** Servicios y acciones que este cliente necesita que el proveedor ofrezca. */
    private static final Map<String, List<String>> REQUERIDOS = Map.of(
            Servicios.PARTIDA, List.of(Acciones.CREAR, Acciones.UNIRSE, Acciones.INICIAR, Acciones.ESTADO),
            Servicios.JUGADOR, List.of(Acciones.REGISTRAR, Acciones.LISTO, Acciones.ABANDONAR, Acciones.REANUDAR),
            Servicios.TURNO, List.of(Acciones.LANZAR_DADO, Acciones.MOVER_FICHA));

    private final ClienteTcp tcp;

    public ParchisRemoto(ClienteTcp tcp) {
        this.tcp = tcp;
    }

    /** Consulta el catálogo del proveedor ({@code sistema.describir}): {servicio: [acciones]}. */
    public CompletableFuture<Map<String, List<String>>> describir() {
        return tcp.solicitar(Servicios.SISTEMA, Acciones.DESCRIBIR, Map.of())
                .thenApply(r -> catalogo(r.payload().get(Campos.SERVICIOS)));
    }

    /** Falla con {@link ErrorNegocioException} si el proveedor no ofrece todo lo que este cliente usa. */
    public CompletableFuture<Void> verificarCompatibilidad() {
        return describir().thenAccept(catalogo -> {
            for (var requerido : REQUERIDOS.entrySet()) {
                List<String> ofrecidas = catalogo.getOrDefault(requerido.getKey(), List.of());
                if (!ofrecidas.containsAll(requerido.getValue())) {
                    throw new CompletionException(new ErrorNegocioException("Servidor incompatible",
                            "El servidor no ofrece el servicio '" + requerido.getKey() + "' completo."));
                }
            }
        });
    }

    public CompletableFuture<Sobre> crearPartida(int maxJugadores) {
        return tcp.solicitar(Servicios.PARTIDA, Acciones.CREAR, mapa(Campos.MAX_JUGADORES, maxJugadores));
    }

    public CompletableFuture<Sobre> estado() {
        return tcp.solicitar(Servicios.PARTIDA, Acciones.ESTADO, Map.of());
    }

    public CompletableFuture<Sobre> unirse() {
        return tcp.solicitar(Servicios.PARTIDA, Acciones.UNIRSE, Map.of());
    }

    public CompletableFuture<Sobre> iniciarPartida() {
        return tcp.solicitar(Servicios.PARTIDA, Acciones.INICIAR, Map.of());
    }

    public CompletableFuture<Sobre> registrar(String nombre, String color, String avatar) {
        Map<String, Object> p = new HashMap<>();
        p.put(Campos.NOMBRE, nombre);
        p.put(Campos.COLOR, color);
        p.put(Campos.AVATAR, avatar);
        return tcp.solicitar(Servicios.JUGADOR, Acciones.REGISTRAR, p);
    }

    public CompletableFuture<Sobre> marcarListo(boolean listo) {
        return tcp.solicitar(Servicios.JUGADOR, Acciones.LISTO, mapa(Campos.LISTO, listo));
    }

    public CompletableFuture<Sobre> abandonar() {
        return tcp.solicitar(Servicios.JUGADOR, Acciones.ABANDONAR, Map.of());
    }

    public CompletableFuture<Sobre> reanudar(String nombre, String color) {
        Map<String, Object> p = new HashMap<>();
        p.put(Campos.NOMBRE, nombre);
        p.put(Campos.COLOR, color);
        return tcp.solicitar(Servicios.JUGADOR, Acciones.REANUDAR, p);
    }

    public CompletableFuture<ResultadoTiro> lanzarDado() {
        return tcp.solicitar(Servicios.TURNO, Acciones.LANZAR_DADO, Map.of()).thenApply(ResultadoTiro::de);
    }

    public CompletableFuture<Sobre> moverFicha(int idFicha) {
        return tcp.solicitar(Servicios.TURNO, Acciones.MOVER_FICHA, mapa(Campos.ID_FICHA, idFicha));
    }

    private static Map<String, Object> mapa(String k, Object v) {
        Map<String, Object> m = new HashMap<>();
        m.put(k, v);
        return m;
    }

    private static Map<String, List<String>> catalogo(Object o) {
        Map<String, List<String>> r = new HashMap<>();
        if (o instanceof Map<?, ?> m) {
            for (var e : m.entrySet()) {
                List<String> acciones = new ArrayList<>();
                if (e.getValue() instanceof List<?> l) {
                    l.forEach(a -> acciones.add(String.valueOf(a)));
                }
                r.put(String.valueOf(e.getKey()), acciones);
            }
        }
        return r;
    }
}
