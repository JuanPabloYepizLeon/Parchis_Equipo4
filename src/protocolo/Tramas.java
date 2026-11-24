package protocolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Lee y escribe los mensajes en la conexión, uno por línea.
 */
public final class Tramas {

    public static final int MAX_BYTES_TRAMA = 64 * 1024;

    private Tramas() {
    }

    /** Lee los mensajes que llegan por la conexión. */
    public static final class Lector {

        private final BufferedReader in;

        public Lector(InputStream is) {
            this.in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        }

        public String siguiente() throws IOException {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = in.read()) != -1) {
                if (c == '\n') {
                    int n = sb.length();
                    if (n > 0 && sb.charAt(n - 1) == '\r') {
                        sb.setLength(n - 1);
                    }
                    return sb.toString();
                }
                if (sb.length() >= MAX_BYTES_TRAMA) {
                    throw new IOException("Trama mayor al máximo permitido");
                }
                sb.append((char) c);
            }
            return null;
        }
    }

    /** Escribe mensajes en la conexión. */
    public static final class Escritor {

        private final Writer out;

        public Escritor(OutputStream os) {
            this.out = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        }

        public void escribir(Sobre s) throws IOException {
            out.write(s.aJson());
            out.write('\n');
            out.flush();
        }
    }
}
