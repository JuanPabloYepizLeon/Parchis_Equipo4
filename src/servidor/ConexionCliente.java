package servidor;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Jugador;
import protocolo.Contrato.Servicios;
import protocolo.ProtocoloException;
import protocolo.Sobre;
import protocolo.Tramas;

/**
 * Conexión de un jugador con el servidor: recibe sus peticiones y le envía respuestas y eventos.
 */
public final class ConexionCliente implements Runnable {

    private static final Logger LOG = Logger.getLogger(ConexionCliente.class.getName());
    private static final Sobre FIN = Sobre.evento("fin", Servicios.SISTEMA, "FIN", null);
    private static final int CAPACIDAD_COLA = 512;
    private static final int TIMEOUT_LECTURA_MS = 15_000;

    private final Socket socket;
    private final ServidorTcp servidor;
    private final EnrutadorServicios enrutador;
    private final BlockingQueue<Sobre> salida = new LinkedBlockingQueue<>(CAPACIDAD_COLA);
    private volatile Jugador jugador;
    private volatile boolean anfitrion;
    private volatile boolean cerrada;

    ConexionCliente(Socket socket, ServidorTcp servidor, EnrutadorServicios enrutador) {
        this.socket = socket;
        this.servidor = servidor;
        this.enrutador = enrutador;
    }

    @Override
    public void run() {
        Thread.ofVirtual().name("escritor-" + socket.getRemoteSocketAddress()).start(this::bucleEscritura);
        try {
            socket.setSoTimeout(TIMEOUT_LECTURA_MS);
            Tramas.Lector lector = new Tramas.Lector(socket.getInputStream());
            String trama;
            while ((trama = lector.siguiente()) != null) {
                Sobre req = null;
                Sobre resp;
                try {
                    req = Sobre.deJson(trama);
                    resp = enrutador.enrutar(req, this);
                } catch (ProtocoloException e) {
                    resp = Sobre.error(req, e.getCodigo(), "Error de protocolo", e.getMessage(), null);
                }
                enviar(resp);
            }
        } catch (IOException e) {
            if (!cerrada) {
                LOG.log(Level.FINE, "Conexión interrumpida", e);
            }
        } finally {
            cerrar();
        }
    }

    private void bucleEscritura() {
        try {
            Tramas.Escritor escritor = new Tramas.Escritor(socket.getOutputStream());
            while (true) {
                Sobre s = salida.take();
                if (s == FIN) {
                    return;
                }
                escritor.escribir(s);
            }
        } catch (IOException | InterruptedException e) {
            cerrar();
        }
    }

    public void enviar(Sobre s) {
        if (cerrada) {
            return;
        }
        if (!salida.offer(s)) {
            LOG.warning("Cola de salida llena; cerrando cliente lento");
            cerrar();
        }
    }

    public void cerrar() {
        if (cerrada) {
            return;
        }
        cerrada = true;
        salida.offer(FIN);
        try {
            socket.close();
        } catch (IOException ignorada) {
        }
        servidor.conexionCerrada(this);
    }

    public Jugador jugador() {
        return jugador;
    }

    public void asignarJugador(Jugador j) {
        this.jugador = j;
    }

    public String direccionRemota() {
        return socket.getInetAddress().getHostAddress();
    }

    public boolean esAnfitrion() {
        return anfitrion;
    }

    public void marcarAnfitrion() {
        this.anfitrion = true;
    }
}
