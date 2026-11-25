package cliente;

import comun.ErrorNegocioException;
import comun.Hospedaje;
import comun.ServidorHospedado;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import modelo.Color;
import modelo.EstadoPartida;
import protocolo.Sobre;

/**
 * Sesión del jugador: guarda su conexión, sus datos y la copia de la partida.
 * Si el jugador es el anfitrión, también arranca el servidor en su equipo.
 */
public final class SesionCliente {

    private static final int INTENTOS_RECONEXION = 10;
    private static final String LOCALHOST = "127.0.0.1";

    private final Hospedaje hospedaje;
    private final ReplicaPartida replica = new ReplicaPartida();
    private volatile ClienteTcp tcp;
    private volatile ParchisRemoto remoto;
    private volatile ServidorHospedado servidorLocal;
    private volatile boolean anfitrion;
    private volatile int puerto;
    private volatile String nombreLocal;
    private volatile Color colorLocal;
    private volatile Runnable alPerderConexion = () -> {
    };

    public SesionCliente(Hospedaje hospedaje) {
        this.hospedaje = hospedaje;
    }

    /** Arranca el servidor en este equipo, se conecta a él y crea la partida. */
    public CompletableFuture<Void> hospedarPartida(int puertoServidor, int maxJugadores) {
        return CompletableFuture.supplyAsync(() -> arrancarServidor(puertoServidor, null, null))
                .thenCompose(srv -> {
                    servidorLocal = srv;
                    return conectar(LOCALHOST, srv.puerto());
                })
                .thenCompose(v -> remoto.crearPartida(maxJugadores))
                .thenAccept(r -> {
                    anfitrion = true;
                    replica.sembrar(r);
                });
    }

    /**
     * Se conecta al servidor de otro jugador, comprueba en su catálogo que ofrece los servicios que este cliente
     * usa y solicita un lugar en su partida.
     */
    public CompletableFuture<Void> unirse(String direccion, int puertoServidor) {
        return conectar(direccion, puertoServidor)
                .thenCompose(v -> remoto.verificarCompatibilidad())
                .thenCompose(v -> remoto.unirse())
                .thenAccept(r -> {
                    anfitrion = false;
                    replica.sembrar(r);
                });
    }

    private CompletableFuture<Void> conectar(String host, int puertoServidor) {
        cerrarConexion();
        this.puerto = puertoServidor;
        return ClienteTcp.conectar(host, puertoServidor, HiloUi.EDT).thenAccept(t -> {
            tcp = t;
            remoto = new ParchisRemoto(t);
            t.suscribir(replica::aplicar);
            t.alDesconectar(() -> {
                if (tcp == t) {
                    alPerderConexion.run();
                }
            });
        });
    }

    private ServidorHospedado arrancarServidor(int puerto, Map<String, Object> snapshot, Color nuevoAnfitrion) {
        try {
            return snapshot == null
                    ? hospedaje.arrancar(puerto)
                    : hospedaje.arrancarRestaurando(puerto, snapshot, nuevoAnfitrion.name());
        } catch (ErrorNegocioException e) {
            throw new CompletionException(e);
        }
    }

    private void cerrarConexion() {
        ClienteTcp t = tcp;
        tcp = null;
        remoto = null;
        if (t != null) {
            t.close();
        }
    }

    private void cerrarServidorLocal() {
        ServidorHospedado s = servidorLocal;
        servidorLocal = null;
        if (s != null) {
            s.close();
        }
    }

    public void cerrar() {
        cerrarConexion();
        replica.reiniciar();
        cerrarServidorLocal();
        anfitrion = false;
        nombreLocal = null;
        colorLocal = null;
    }

    public ReplicaPartida replica() {
        return replica;
    }

    public ParchisRemoto remoto() {
        return remoto;
    }

    public ServidorHospedado servidorLocal() {
        return servidorLocal;
    }

    public boolean esAnfitrion() {
        return anfitrion;
    }

    public Color colorLocal() {
        return colorLocal;
    }

    public void jugadorLocal(String nombre, Color color) {
        this.nombreLocal = nombre;
        this.colorLocal = color;
    }

    public void alPerderConexion(Runnable r) {
        this.alPerderConexion = r;
    }

    public CompletableFuture<Boolean> recuperarPartida() {
        PartidaVista partida = replica.actual();
        Map<String, Object> snap = replica.crudo();
        Color miColor = colorLocal;
        String miNombre = nombreLocal;
        if (miColor == null || anfitrion || partida.estado() == EstadoPartida.TERMINADA) {
            return CompletableFuture.completedFuture(false);
        }
        List<PartidaVista.JugadorVista> candidatos = partida.candidatosAnfitrion();
        boolean sigoEnPartida = candidatos.stream().anyMatch(j -> j.color() == miColor);
        if (!sigoEnPartida) {
            return CompletableFuture.completedFuture(false);
        }
        PartidaVista.JugadorVista primero = candidatos.get(0);
        int puertoServidor = puerto;

        if (primero.color() == miColor) {
            return CompletableFuture.supplyAsync(() -> arrancarServidor(puertoServidor, snap, miColor))
                    .thenCompose(srv -> {
                        servidorLocal = srv;
                        return conectar(LOCALHOST, srv.puerto());
                    })
                    .thenCompose(v -> remoto.reanudar(miNombre, miColor.name()))
                    .thenApply(r -> {
                        anfitrion = true;
                        HiloUi.EDT.execute(() -> replica.reemplazar(r.payload(), ReplicaPartida.PARTIDA_RECUPERADA));
                        return true;
                    })
                    .exceptionally(ex -> {
                        cerrarServidorLocal();
                        return false;
                    });
        }
        return CompletableFuture.supplyAsync(() -> reconectar(primero.direccion(), puertoServidor, miNombre, miColor));
    }

    private boolean reconectar(String direccion, int puertoServidor, String nombre, Color color) {
        for (int intento = 0; intento < INTENTOS_RECONEXION; intento++) {
            try {
                conectar(direccion, puertoServidor).get(4, TimeUnit.SECONDS);
                Sobre r = remoto.reanudar(nombre, color.name()).get(4, TimeUnit.SECONDS);
                HiloUi.EDT.execute(() -> replica.reemplazar(r.payload(), ReplicaPartida.PARTIDA_RECUPERADA));
                return true;
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
}
