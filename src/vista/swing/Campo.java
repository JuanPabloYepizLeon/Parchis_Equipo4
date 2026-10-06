package vista.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class Campo extends JPanel {

    private static final Color ROJO = new Color(0xD3, 0x2F, 0x2F);
    private static final Color GRIS_BORDE = new Color(0xCC, 0xCC, 0xCC);

    private final JLabel lblEtiqueta = new JLabel();
    private final JTextField txt = new JTextField();
    private final JLabel lblError = new JLabel(" ");

    public Campo() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);

        lblEtiqueta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblEtiqueta.setAlignmentX(LEFT_ALIGNMENT);

        txt.setAlignmentX(LEFT_ALIGNMENT);
        txt.setPreferredSize(new Dimension(300, 32));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        lblError.setForeground(ROJO);
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblError.setAlignmentX(LEFT_ALIGNMENT);

        add(lblEtiqueta);
        add(Box.createVerticalStrut(4));
        add(txt);
        add(Box.createVerticalStrut(2));
        add(lblError);
        limpiarError();
    }

    public Campo(String etiqueta) {
        this();
        setEtiqueta(etiqueta);
    }

    public String getEtiqueta() {
        return lblEtiqueta.getText();
    }

    public void setEtiqueta(String etiqueta) {
        lblEtiqueta.setText(etiqueta);
    }

    public String texto() {
        return txt.getText();
    }

    public void error(String mensaje) {
        txt.setBorder(borde(ROJO));
        lblError.setText(mensaje);
    }

    public void limpiarError() {
        txt.setBorder(borde(GRIS_BORDE));
        lblError.setText(" ");
    }

    private static Border borde(Color c) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(c), BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }
}
