package servidor;

import comun.ErrorNegocioException;
import comun.ServidorHospedado;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocolo.Contrato.Eventos;
import protocolo.Contrato.Servicios;
import protocolo.Sobre;

/**
 * Servidor que acepta las conexiones de los jugadores y les envía mensajes.
 */
public final class ServidorTcp implements ServidorHospedado {

    private static final Logger LOG = Logger.getLogger(ServidorTcp.class.getName());

    private final ServerSocket serverSocket;
    private final EnrutadorServicios enrutador;
    private final Set<ConexionCliente> conexiones = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService latidos = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "servidor-latidos");
        t.setDaemon(true);
        return t;
    });
    private volatile boolean cerrado;
    private volatile Consumer<ConexionCliente> alDesconectar = c -> {
    };

    private ServidorTcp(ServerSocket ss, EnrutadorServicios enrutador) {
        this.serverSocket = ss;
        this.enrutador = enrutador;
    }

    public static ServidorTcp iniciar(int puerto, EnrutadorServicios enrutador) throws ErrorNegocioException {
        try {
            ServerSocket ss = new ServerSocket();
            ss.setReuseAddress(true);
            ss.bind(new InetSocketAddress(puerto));
            ServidorTcp s = new ServidorTcp(ss, enrutador);
            Thread.ofPlatform().daemon().name("aceptador-" + ss.getLocalPort()).start(s::bucleAceptar);
            s.latidos.scheduleAtFixedRate(() -> s.difundir(Sobre.evento("latido", Servicios.SISTEMA, Eventos.LATIDO, null)),
                    5, 5, TimeUnit.SECONDS);
            return s;
        } catch (IOException | IllegalArgumentException e) {
            throw new ErrorNegocioException("No se pudo crear la partida",
                    "El puerto ya está en uso o no es válido. Elige otro puerto e inténtalo de nuevo.");
        }
    }

    private void bucleAceptar() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                ConexionCliente c = new ConexionCliente(socket, this, enrutador);
                conexiones.add(c);
                Thread.ofVirtual().name("cliente-" + socket.getRemoteSocketAddress()).start(c);
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    LOG.log(Level.WARNING, "Error aceptando conexión", e);
                }
            }
        }
    }

    public void difundir(Sobre s) {
        for (ConexionCliente c : conexiones) {
            c.enviar(s);
        }
    }

    void conexionCerrada(ConexionCliente c) {
        if (conexiones.remove(c) && !cerrado) {
            alDesconectar.accept(c);
        }
    }

    public void alDesconectar(Consumer<ConexionCliente> accion) {
        this.alDesconectar = accion;
    }

    @Override
    public int puerto() {
        return serverSocket.getLocalPort();
    }

    @Override
    public List<String> direccionesLocales() {
        return DireccionesLocales.listar();
    }

    @Override
    public void close() {
        cerrado = true;
        latidos.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException ignorada) {
            // nada que hacer
        }
        for (ConexionCliente c : conexiones) {
            c.cerrar();
        }
    }
}
