package servidor;

import comun.ErrorNegocioException;
import comun.ValidacionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import protocolo.Contrato.Errores;
import protocolo.ProtocoloException;

/**
 * Base para crear servicios: guarda sus acciones y ejecuta la que se pida.
 */
public abstract class ServicioBase implements Servicio {

    private final String nombre;
    private final Map<String, Accion> acciones = new HashMap<>();

    protected ServicioBase(String nombre) {
        this.nombre = nombre;
    }

    protected final void registrar(String accion, Accion impl) {
        acciones.put(accion, impl);
    }

    @Override
    public final String nombre() {
        return nombre;
    }

    @Override
    public final Set<String> acciones() {
        return new TreeSet<>(acciones.keySet());
    }

    @Override
    public final Map<String, Object> invocar(String accion, Map<String, Object> payload, ContextoPeticion ctx)
            throws ErrorNegocioException, ValidacionException {
        Accion a = acciones.get(accion);
        if (a == null) {
            throw new ProtocoloException(Errores.ACCION_DESCONOCIDA,
                    "El servicio '" + nombre + "' no tiene la acción '" + accion + "'");
        }
        return a.ejecutar(payload, ctx);
    }

    protected static String texto(Map<String, Object> p, String clave) throws ValidacionException {
        Object o = p.get(clave);
        if (o == null) {
            return null;
        }
        if (!(o instanceof String s)) {
            throw new ValidacionException(clave, "El campo '" + clave + "' debe ser texto");
        }
        return s;
    }

    protected static Integer entero(Map<String, Object> p, String clave) throws ValidacionException {
        Object o = p.get(clave);
        if (o == null) {
            return null;
        }
        if (o instanceof Long || o instanceof Integer) {
            return ((Number) o).intValue();
        }
        throw new ValidacionException(clave, "El campo '" + clave + "' debe ser un entero");
    }
}
