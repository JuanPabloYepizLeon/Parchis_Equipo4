package servidor;

import comun.ErrorNegocioException;
import comun.ValidacionException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocolo.Contrato.Errores;
import protocolo.ProtocoloException;
import protocolo.Sobre;

/**
 * Recibe cada petición y la manda al servicio que corresponde. También arma la lista de servicios.
 */
public final class EnrutadorServicios {

    private static final Logger LOG = Logger.getLogger(EnrutadorServicios.class.getName());
    private final Map<String, Servicio> servicios = new ConcurrentHashMap<>();

    public EnrutadorServicios registrar(Servicio s) {
        servicios.put(s.nombre(), s);
        return this;
    }

    /** Catálogo de servicios y acciones disponibles, ordenado: {servicio: [acciones]}. */
    public Map<String, Object> catalogo() {
        Map<String, Object> r = new TreeMap<>();
        for (Servicio s : servicios.values()) {
            r.put(s.nombre(), new ArrayList<>(s.acciones()));
        }
        return r;
    }

    public Sobre enrutar(Sobre req, ConexionCliente origen) {
        try {
            if (req.tipo() != Sobre.Tipo.REQUEST) {
                throw new ProtocoloException(Errores.SOBRE_INVALIDO, "Solo se aceptan REQUEST del cliente");
            }
            Servicio s = servicios.get(req.service());
            if (s == null) {
                throw new ProtocoloException(Errores.SERVICIO_DESCONOCIDO,
                        "No existe el servicio '" + req.service() + "'");
            }
            Map<String, Object> salida = s.invocar(req.action(), req.payload(), new ContextoPeticion(origen));
            return Sobre.ok(req, salida);
        } catch (ErrorNegocioException e) {
            return Sobre.error(req, Errores.NEGOCIO, e.titulo, e.getMessage(), null);
        } catch (ValidacionException e) {
            return Sobre.error(req, Errores.VALIDACION, "Datos no válidos", e.getMessage(), e.campo);
        } catch (ProtocoloException e) {
            return Sobre.error(req, e.getCodigo(), "Error de protocolo", e.getMessage(), null);
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error interno en " + req.service() + "." + req.action(), e);
            return Sobre.error(req, Errores.INTERNO, "Error interno", "Ocurrió un error en el servidor.", null);
        }
    }
}
