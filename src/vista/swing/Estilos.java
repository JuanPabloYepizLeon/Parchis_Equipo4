package vista.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Estilo común de las ventanas: colores, botones y mensajes emergentes.
 */
final class Estilos {

    static final Color AZUL_OSCURO = new Color(0x2F, 0x34, 0x4A);
    static final Color ROJO_ERROR = new Color(0xD3, 0x2F, 0x2F);
    static final Color GRIS_BORDE = new Color(0xCC, 0xCC, 0xCC);
    private static final int ALTO_BOTON = 40;
    private static final int ANCHO_MODAL = 300;

    private Estilos() {
    }

    static void primario(JButton b) {
        comun(b);
        b.setBackground(AZUL_OSCURO);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder());
    }

    static void secundario(JButton b) {
        comun(b);
        b.setBackground(Color.WHITE);
        b.setForeground(AZUL_OSCURO);
        b.setBorder(BorderFactory.createLineBorder(AZUL_OSCURO, 1));
    }

    /** Alinea el botón a la izquierda y lo estira a todo el ancho del formulario. */
    static void anchoCompleto(JButton b, int ancho) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setPreferredSize(new Dimension(ancho, ALTO_BOTON));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_BOTON));
    }

    static void mostrarModal(Window duena, String titulo, String mensaje) {
        JDialog d = new JDialog(duena, titulo, JDialog.ModalityType.APPLICATION_MODAL);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMensaje = new JLabel("<html><body style='width:240px'>" + mensaje + "</body></html>");
        lblMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton aceptar = new JButton("Aceptar");
        primario(aceptar);
        anchoCompleto(aceptar, ANCHO_MODAL);
        aceptar.addActionListener(e -> d.dispose());

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        p.add(lblTitulo);
        p.add(Box.createVerticalStrut(8));
        p.add(lblMensaje);
        p.add(Box.createVerticalStrut(16));
        p.add(aceptar);

        d.setContentPane(p);
        d.getRootPane().setDefaultButton(aceptar);
        d.setResizable(false);
        d.pack();
        d.setLocationRelativeTo(duena);
        d.setVisible(true);
    }

    private static void comun(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
