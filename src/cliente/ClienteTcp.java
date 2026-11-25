package cliente;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import protocolo.Contrato.Acciones;
import protocolo.Contrato.Servicios;
import protocolo.ProtocoloException;
import protocolo.Sobre;
import protocolo.Tramas;

/**
 * Conexión del jugador con el servidor.
 * Envía las peticiones, espera sus respuestas y entrega los eventos que manda el servidor.
 */
public final class ClienteTcp implements AutoCloseable {

    private static final long TIMEOUT_SEG = 5;
    private static final int TIMEOUT_LECTURA_MS = 15_000;
    private static final long PERIODO_PING_SEG = 5;

    private final Socket socket;
    private final Executor ui;
    private final ExecutorService escritor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "cliente-escritor");
        t.setDaemon(true);
        return t;
    });
    private final ScheduledExecutorService pings = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "cliente-ping");
        t.setDaemon(true);
        return t;
    });
    private final Map<String, CompletableFuture<Sobre>> pendientes = new ConcurrentHashMap<>();
    private final List<Consumer<Sobre>> oyentes = new CopyOnWriteArrayList<>();
    private final AtomicLong secuencia = new AtomicLong();
    private final String idCliente = Long.toString(System.nanoTime() % 100_000, 36);
    private final Tramas.Escritor salida;
    private volatile Runnable alDesconectar = () -> {
    };

    private ClienteTcp(Socket socket, Executor ui) throws IOException {
        this.socket = socket;
        this.ui = ui;
        this.salida = new Tramas.Escritor(socket.getOutputStream());
        socket.setSoTimeout(TIMEOUT_LECTURA_MS);
        pings.scheduleAtFixedRate(() -> solicitar(Servicios.SISTEMA, Acciones.PING, Map.of()), PERIODO_PING_SEG, PERIODO_PING_SEG,
                TimeUnit.SECONDS);
        Thread lector = new Thread(this::bucleLectura, "cliente-lector");
        lector.setDaemon(true);
        lector.start();
    }

    public static CompletableFuture<ClienteTcp> conectar(String host, int puerto, Executor ui) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Socket s = new Socket();
                s.setTcpNoDelay(true);
                s.connect(new InetSocketAddress(host, puerto), 3000);
                return new ClienteTcp(s, ui);
            } catch (IOException | IllegalArgumentException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Sobre> solicitar(String servicio, String accion, Map<String, Object> payload) {
        String id = idCliente + "-" + secuencia.incrementAndGet();
        CompletableFuture<Sobre> futura = new CompletableFuture<Sobre>().orTimeout(TIMEOUT_SEG, TimeUnit.SECONDS);
        pendientes.put(id, futura);
        futura.whenComplete((r, e) -> pendientes.remove(id));
        Sobre req = Sobre.peticion(id, servicio, accion, payload);
        escritor.execute(() -> {
            try {
                salida.escribir(req);
            } catch (IOException e) {
                futura.completeExceptionally(e);
            }
        });
        return futura;
    }

    public void suscribir(Consumer<Sobre> oyente) {
        oyentes.add(oyente);
    }

    public void alDesconectar(Runnable accion) {
        this.alDesconectar = accion;
    }

    private void bucleLectura() {
        try {
            Tramas.Lector lector = new Tramas.Lector(socket.getInputStream());
            String trama;
            while ((trama = lector.siguiente()) != null) {
                try {
                    despachar(Sobre.deJson(trama));
                } catch (ProtocoloException e) {
                }
            }
        } catch (IOException ignorada) {
        } finally {
            CompletionException caida = new CompletionException(new IOException("Conexión cerrada"));
            pendientes.values().forEach(f -> f.completeExceptionally(caida));
            pings.shutdownNow();
            ui.execute(() -> alDesconectar.run());
            escritor.shutdownNow();
        }
    }

    private void despachar(Sobre s) {
        switch (s.tipo()) {
            case RESPONSE -> {
                CompletableFuture<Sobre> f = s.messageId() == null ? null : pendientes.get(s.messageId());
                if (f == null) {
                    return;
                }
                if (s.esOk()) {
                    f.complete(s);
                } else {
                    f.completeExceptionally(new ErrorServicioException(s));
                }
            }
            case EVENT -> {
                if (Servicios.SISTEMA.equals(s.service())) {
                    return;
                }
                for (Consumer<Sobre> o : oyentes) {
                    ui.execute(() -> o.accept(s));
                }
            }
            default -> {
            }
        }
    }

    @Override
    public void close() {
        pings.shutdownNow();
        try {
            socket.close();
        } catch (IOException ignorada) {
        }
    }
}
