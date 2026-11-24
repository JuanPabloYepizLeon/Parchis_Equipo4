package protocolo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Convierte datos a texto JSON y de regreso.
 */
public final class MiniJson {

    private static final int PROFUNDIDAD_MAX = 32;

    private MiniJson() {
    }

    public static String escribir(Object valor) {
        StringBuilder sb = new StringBuilder();
        escribir(sb, valor);
        return sb.toString();
    }

    private static void escribir(StringBuilder sb, Object o) {
        if (o == null) {
            sb.append("null");
        } else if (o instanceof String s) {
            cadena(sb, s);
        } else if (o instanceof Boolean || o instanceof Integer || o instanceof Long) {
            sb.append(o);
        } else if (o instanceof Number n) {
            double d = n.doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                throw new IllegalArgumentException("Número no serializable");
            }
            sb.append(n);
        } else if (o instanceof Enum<?> e) {
            cadena(sb, e.name());
        } else if (o instanceof Map<?, ?> m) {
            sb.append('{');
            boolean primero = true;
            for (Map.Entry<?, ?> en : m.entrySet()) {
                if (!primero) {
                    sb.append(',');
                }
                primero = false;
                cadena(sb, String.valueOf(en.getKey()));
                sb.append(':');
                escribir(sb, en.getValue());
            }
            sb.append('}');
        } else if (o instanceof Iterable<?> it) {
            sb.append('[');
            boolean primero = true;
            for (Object e : it) {
                if (!primero) {
                    sb.append(',');
                }
                primero = false;
                escribir(sb, e);
            }
            sb.append(']');
        } else {
            throw new IllegalArgumentException("Tipo no serializable: " + o.getClass().getName());
        }
    }

    private static void cadena(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' ->
                    sb.append("\\\"");
                case '\\' ->
                    sb.append("\\\\");
                case '\n' ->
                    sb.append("\\n");
                case '\r' ->
                    sb.append("\\r");
                case '\t' ->
                    sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }

    public static Object leer(String json) {
        Lector l = new Lector(json);
        l.espacios();
        Object v = l.valor(0);
        l.espacios();
        if (l.i != json.length()) {
            throw l.error("Texto sobrante después del JSON");
        }
        return v;
    }

    /** Recorre el texto JSON y lo convierte en datos. */
    private static final class Lector {

        final String s;
        int i = 0;

        Lector(String s) {
            this.s = s;
        }

        ProtocoloException error(String msg) {
            return new ProtocoloException(Contrato.Errores.JSON_INVALIDO, msg + " (posición " + i + ")");
        }

        void espacios() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
        }

        char actual() {
            if (i >= s.length()) {
                throw error("Fin inesperado");
            }
            return s.charAt(i);
        }

        void esperar(char c) {
            if (actual() != c) {
                throw error("Se esperaba '" + c + "'");
            }
            i++;
        }

        Object valor(int prof) {
            if (prof > PROFUNDIDAD_MAX) {
                throw error("Anidamiento excesivo");
            }
            char c = actual();
            if (c == '{') {
                return objeto(prof);
            }
            if (c == '[') {
                return arreglo(prof);
            }
            if (c == '"') {
                return texto();
            }
            if (s.startsWith("true", i)) {
                i += 4;
                return Boolean.TRUE;
            }
            if (s.startsWith("false", i)) {
                i += 5;
                return Boolean.FALSE;
            }
            if (s.startsWith("null", i)) {
                i += 4;
                return null;
            }
            return numero();
        }

        Map<String, Object> objeto(int prof) {
            Map<String, Object> m = new LinkedHashMap<>();
            esperar('{');
            espacios();
            if (actual() == '}') {
                i++;
                return m;
            }
            while (true) {
                espacios();
                String k = texto();
                espacios();
                esperar(':');
                espacios();
                m.put(k, valor(prof + 1));
                espacios();
                if (actual() == ',') {
                    i++;
                    continue;
                }
                esperar('}');
                return m;
            }
        }

        List<Object> arreglo(int prof) {
            List<Object> l = new ArrayList<>();
            esperar('[');
            espacios();
            if (actual() == ']') {
                i++;
                return l;
            }
            while (true) {
                espacios();
                l.add(valor(prof + 1));
                espacios();
                if (actual() == ',') {
                    i++;
                    continue;
                }
                esperar(']');
                return l;
            }
        }

        String texto() {
            esperar('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = actual();
                i++;
                if (c == '"') {
                    return sb.toString();
                }
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                char e = actual();
                i++;
                switch (e) {
                    case '"', '\\', '/' ->
                        sb.append(e);
                    case 'n' ->
                        sb.append('\n');
                    case 'r' ->
                        sb.append('\r');
                    case 't' ->
                        sb.append('\t');
                    case 'b' ->
                        sb.append('\b');
                    case 'f' ->
                        sb.append('\f');
                    case 'u' -> {
                        if (i + 4 > s.length()) {
                            throw error("Escape \\u incompleto");
                        }
                        try {
                            sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                        } catch (NumberFormatException ex) {
                            throw error("Escape \\u inválido");
                        }
                        i += 4;
                    }
                    default ->
                        throw error("Escape inválido");
                }
            }
        }

        Number numero() {
            int ini = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) {
                i++;
            }
            String t = s.substring(ini, i);
            if (t.isEmpty()) {
                throw error("Valor inesperado");
            }
            try {
                if (t.indexOf('.') >= 0 || t.indexOf('e') >= 0 || t.indexOf('E') >= 0) {
                    return Double.valueOf(t);
                }
                return Long.valueOf(t);
            } catch (NumberFormatException ex) {
                throw error("Número inválido");
            }
        }
    }
}
