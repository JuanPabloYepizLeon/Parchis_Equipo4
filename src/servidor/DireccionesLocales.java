package servidor;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Obtiene las direcciones IP del equipo para que los demás jugadores puedan conectarse.
 */
final class DireccionesLocales {

    private DireccionesLocales() {
    }

    static List<String> listar() {
        List<String> r = new ArrayList<>();
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                for (InetAddress a : Collections.list(ni.getInetAddresses())) {
                    if (a instanceof Inet4Address && a.isSiteLocalAddress()) {
                        r.add(a.getHostAddress() + "  (" + ni.getDisplayName() + ")");
                    }
                }
            }
        } catch (Exception ignorada) {
        }
        return r;
    }
}
