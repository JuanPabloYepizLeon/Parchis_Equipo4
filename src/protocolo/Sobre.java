package protocolo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mensaje que viaja entre cliente y servidor: puede ser una petición, una respuesta o un evento.
 */
public final class Sobre {

    /** Tipo de mensaje: petición, respuesta o evento. */
    public enum Tipo {
        REQUEST, RESPONSE, EVENT
    }

    public static final int VERSION = 1;
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    private final Tipo tipo;
    private final String messageId;
    private final String service;
    private final String action;
    private final String status;
    private final Map<String, Object> payload;
    private final Map<String, Object> error;

    private Sobre(Tipo tipo, String messageId, String service, String action,
            String status, Map<String, Object> payload, Map<String, Object> error) {
        this.tipo = tipo;
        this.messageId = messageId;
        this.service = service;
        this.action = action;
        this.status = status;
        this.payload = payload == null ? Map.of() : payload;
        this.error = error;
    }

    public static Sobre peticion(String messageId, String service, String action, Map<String, Object> payload) {
        return new Sobre(Tipo.REQUEST, messageId, service, action, null, payload, null);
    }

    public static Sobre ok(Sobre peticion, Map<String, Object> payload) {
        return new Sobre(Tipo.RESPONSE, peticion.messageId, peticion.service, peticion.action, OK, payload, null);
    }

    public static Sobre error(Sobre peticion, String codigo, String titulo, String mensaje, String campo) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("code", codigo);
        e.put("title", titulo);
        e.put("message", mensaje);
        e.put("field", campo);
        return new Sobre(Tipo.RESPONSE,
                peticion == null ? null : peticion.messageId,
                peticion == null ? null : peticion.service,
                peticion == null ? null : peticion.action,
                ERROR, Map.of(), e);
    }

    public static Sobre evento(String messageId, String service, String evento, Map<String, Object> payload) {
        return new Sobre(Tipo.EVENT, messageId, service, evento, null, payload, null);
    }

    public String aJson() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("v", VERSION);
        m.put("type", tipo);
        m.put("messageId", messageId);
        m.put("service", service);
        m.put("action", action);
        if (status != null) {
            m.put("status", status);
        }
        m.put("payload", payload);
        if (error != null) {
            m.put("error", error);
        }
        return MiniJson.escribir(m);
    }

    @SuppressWarnings("unchecked")
    public static Sobre deJson(String json) {
        Object raiz = MiniJson.leer(json);
        if (!(raiz instanceof Map<?, ?>)) {
            throw new ProtocoloException(Contrato.Errores.SOBRE_INVALIDO, "El sobre debe ser un objeto JSON");
        }
        Map<String, Object> m = (Map<String, Object>) raiz;
        Object v = m.get("v");
        if (!(v instanceof Number n) || n.intValue() != VERSION) {
            throw new ProtocoloException(Contrato.Errores.SOBRE_INVALIDO, "Versión de protocolo no soportada");
        }
        Tipo tipo;
        try {
            tipo = Tipo.valueOf(String.valueOf(m.get("type")));
        } catch (IllegalArgumentException e) {
            throw new ProtocoloException(Contrato.Errores.SOBRE_INVALIDO, "Campo 'type' inválido");
        }
        String id = texto(m, "messageId");
        String service = texto(m, "service");
        String action = texto(m, "action");
        if (id == null || service == null || action == null) {
            if (tipo != Tipo.RESPONSE) {
                throw new ProtocoloException(Contrato.Errores.SOBRE_INVALIDO,
                        "Faltan messageId, service o action");
            }
        }
        Object p = m.get("payload");
        if (p != null && !(p instanceof Map<?, ?>)) {
            throw new ProtocoloException(Contrato.Errores.SOBRE_INVALIDO, "'payload' debe ser un objeto");
        }
        Object e = m.get("error");
        return new Sobre(tipo, id, service, action, texto(m, "status"),
                (Map<String, Object>) p, e instanceof Map<?, ?> ? (Map<String, Object>) e : null);
    }

    private static String texto(Map<String, Object> m, String k) {
        Object o = m.get(k);
        return o instanceof String s && !s.isBlank() ? s : null;
    }

    public Tipo tipo() {
        return tipo;
    }

    public String messageId() {
        return messageId;
    }

    public String service() {
        return service;
    }

    public String action() {
        return action;
    }

    public String status() {
        return status;
    }

    public Map<String, Object> payload() {
        return payload;
    }

    public Map<String, Object> error() {
        return error;
    }

    public boolean esOk() {
        return OK.equals(status);
    }
}
