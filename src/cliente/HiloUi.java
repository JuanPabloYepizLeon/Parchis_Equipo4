package cliente;

import java.util.concurrent.Executor;
import javax.swing.SwingUtilities;

/**
 * Permite ejecutar código en el hilo de la interfaz gráfica de Swing.
 */
public final class HiloUi {

    public static final Executor EDT = SwingUtilities::invokeLater;

    private HiloUi() {
    }
}
