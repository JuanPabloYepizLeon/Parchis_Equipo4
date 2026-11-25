package vista.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;

/**
 * Círculo del avatar que eligió el jugador al registrarse, con un aro del color de su ficha.
 */
final class IconoAvatar implements Icon {

    private static final int TAM = 38;
    private static final Color GRIS_AVATAR = new Color(0xE5, 0xE7, 0xEB);
    private static final Color DORADO = new Color(0xFF, 0xD7, 0x00);

    private final Color colorJugador;
    private final String numero;
    private final boolean esTurno;
    private final boolean atenuado;

    IconoAvatar(Color colorJugador, String avatar, boolean esTurno, boolean atenuado) {
        this.colorJugador = colorJugador;
        this.numero = avatar == null ? "?" : avatar.replace("avatar", "");
        this.esTurno = esTurno;
        this.atenuado = atenuado;
    }

    @Override
    public void paintIcon(Component c, Graphics g0, int x, int y) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(x, y);
        if (esTurno) {
            g.setColor(DORADO);
            g.setStroke(new BasicStroke(3));
            g.drawOval(1, 1, TAM - 3, TAM - 3);
        }
        g.setColor(atenuado ? GRIS_AVATAR.brighter() : GRIS_AVATAR);
        g.fillOval(5, 5, TAM - 10, TAM - 10);
        g.setColor(atenuado ? Color.LIGHT_GRAY : colorJugador);
        g.setStroke(new BasicStroke(3));
        g.drawOval(5, 5, TAM - 10, TAM - 10);
        g.setColor(atenuado ? Color.GRAY : Estilos.AZUL_OSCURO);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(numero, (TAM - fm.stringWidth(numero)) / 2f, (TAM + fm.getAscent() - fm.getDescent()) / 2f);
        g.dispose();
    }

    @Override
    public int getIconWidth() {
        return TAM;
    }

    @Override
    public int getIconHeight() {
        return TAM;
    }
}
