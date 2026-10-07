package vista.swing;

import controlador.RegistroController;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import modelo.Color;
import vista.VistaRegistro;

public class VentanaRegistro extends javax.swing.JFrame implements VistaRegistro {

    private static final java.awt.Color AZUL_OSCURO = new java.awt.Color(0x2F, 0x34, 0x4A);
    private static final java.awt.Color ROJO_ERROR = new java.awt.Color(0xD3, 0x2F, 0x2F);
    private static final java.awt.Color GRIS_BORDE = new java.awt.Color(0xCC, 0xCC, 0xCC);
    private static final java.awt.Color GRIS_AVATAR = new java.awt.Color(0xE5, 0xE7, 0xEB);
    private static final int NUM_AVATARES = 4;

    private final Map<Color, Circulo> circulosColor = new EnumMap<>(Color.class);
    private final Circulo[] circulosAvatar = new Circulo[NUM_AVATARES];
    private Color colorSeleccionado;
    private String avatarSeleccionado;
    private RegistroController controlador;

    public VentanaRegistro() {
        initComponents();

        lblErrorNombre.setText(" ");
        lblMsgColor.setText(" ");
        lblMsgAvatar.setText(" ");

        bordeNombre(GRIS_BORDE);

        for (Color c : Color.values()) {
            Circulo circ = new Circulo(awt(c), null, () -> seleccionarColor(c));
            circulosColor.put(c, circ);
            filaColores.add(circ);
        }

        for (int i = 0; i < NUM_AVATARES; i++) {
            String id = "avatar" + (i + 1);
            Circulo circ = new Circulo(GRIS_AVATAR, String.valueOf(i + 1), () -> seleccionarAvatar(id));
            circulosAvatar[i] = circ;
            filaAvatares.add(circ);
        }

        estilizarBoton(btnConfirmar);
        btnConfirmar.addActionListener(e -> confirmar());
        getRootPane().setDefaultButton(btnConfirmar);

        setResizable(false);
        pack();
    }

    public void setControlador(RegistroController c) {
        this.controlador = c;
    }

    public void mostrar() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void confirmar() {
        bordeNombre(GRIS_BORDE);
        lblErrorNombre.setText(" ");
        lblMsgColor.setText(" ");
        lblMsgAvatar.setText(" ");
        controlador.registrarJugador(txtNombre.getText(), colorSeleccionado, avatarSeleccionado);
    }

    private void seleccionarColor(Color c) {
        colorSeleccionado = c;
        circulosColor.forEach((color, circ) -> circ.setSeleccionado(color == c));
        lblMsgColor.setText(" ");
    }

    private void seleccionarAvatar(String id) {
        avatarSeleccionado = id;
        for (int i = 0; i < NUM_AVATARES; i++) {
            circulosAvatar[i].setSeleccionado(("avatar" + (i + 1)).equals(id));
        }
        lblMsgAvatar.setText(" ");
    }

    @Override
    public void mostrarColores(Set<Color> disponibles, Set<Color> ocupados) {
        circulosColor.forEach((color, circ) -> circ.setAtenuado(ocupados.contains(color)));
        if (colorSeleccionado != null && ocupados.contains(colorSeleccionado)) {
            colorSeleccionado = null;
            circulosColor.values().forEach(c -> c.setSeleccionado(false));
        }
    }

    @Override
    public void marcarCampoError(String campo, String mensaje) {
        switch (campo) {
            case "nombre" -> {
                bordeNombre(ROJO_ERROR);
                lblErrorNombre.setText(mensaje);
            }
            case "color" ->
                lblMsgColor.setText(mensaje);
            case "avatar" ->
                lblMsgAvatar.setText(mensaje);
            default ->
                JOptionPane.showMessageDialog(this, mensaje);
        }
    }

    @Override
    public void mostrarMensajeColor(String mensaje) {
        lblMsgColor.setText(mensaje);
    }

    private void bordeNombre(java.awt.Color c) {
        Border b = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(c), BorderFactory.createEmptyBorder(4, 8, 4, 8));
        txtNombre.setBorder(b);
    }

    private void estilizarBoton(JButton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setPreferredSize(new Dimension(300, 40));
        b.setBackground(AZUL_OSCURO);
        b.setForeground(java.awt.Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static java.awt.Color awt(Color c) {
        return switch (c) {
            case ROJO ->
                new java.awt.Color(0xD6, 0x33, 0x33);
            case AZUL ->
                new java.awt.Color(0x2F, 0x6B, 0xD6);
            case AMARILLO ->
                new java.awt.Color(0xE8, 0xC1, 0x2B);
            case VERDE ->
                new java.awt.Color(0x3F, 0xA7, 0x4F);
        };
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        lblNombre = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 4), new java.awt.Dimension(0, 4), new java.awt.Dimension(32767, 4));
        txtNombre = new javax.swing.JTextField();
        lblErrorNombre = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        lblColor = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 4), new java.awt.Dimension(0, 4), new java.awt.Dimension(32767, 4));
        filaColores = new javax.swing.JPanel();
        lblAyudaColor = new javax.swing.JLabel();
        lblMsgColor = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        lblAvatar = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 4), new java.awt.Dimension(0, 4), new java.awt.Dimension(32767, 4));
        filaAvatares = new javax.swing.JPanel();
        lblMsgAvatar = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 16), new java.awt.Dimension(0, 16), new java.awt.Dimension(32767, 16));
        btnConfirmar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de jugador");
        setResizable(false);

        panel.setBackground(new java.awt.Color(255, 255, 255));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 32, 24, 32));
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));

        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        lblTitulo.setText("Registro de jugador");
        panel.add(lblTitulo);
        panel.add(filler1);

        lblNombre.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblNombre.setText("Nombre");
        panel.add(lblNombre);
        panel.add(filler2);

        txtNombre.setAlignmentX(0.0F);
        txtNombre.setMaximumSize(new java.awt.Dimension(2147483647, 32));
        txtNombre.setPreferredSize(new java.awt.Dimension(300, 32));
        panel.add(txtNombre);

        lblErrorNombre.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblErrorNombre.setForeground(new java.awt.Color(211, 47, 47));
        lblErrorNombre.setToolTipText("");
        panel.add(lblErrorNombre);
        panel.add(filler3);

        lblColor.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblColor.setText("Color");
        panel.add(lblColor);
        panel.add(filler4);

        filaColores.setAlignmentX(0.0F);
        filaColores.setOpaque(false);
        filaColores.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        panel.add(filaColores);

        lblAyudaColor.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblAyudaColor.setForeground(new java.awt.Color(128, 128, 128));
        lblAyudaColor.setText("Los colores atenuados ya fueron tomados por otro jugador.");
        panel.add(lblAyudaColor);

        lblMsgColor.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblMsgColor.setForeground(new java.awt.Color(211, 47, 47));
        panel.add(lblMsgColor);
        panel.add(filler5);

        lblAvatar.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblAvatar.setText("Avatar");
        panel.add(lblAvatar);
        panel.add(filler6);

        filaAvatares.setAlignmentX(0.0F);
        filaAvatares.setOpaque(false);
        filaAvatares.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        panel.add(filaAvatares);

        lblMsgAvatar.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblMsgAvatar.setForeground(new java.awt.Color(211, 47, 47));
        panel.add(lblMsgAvatar);
        panel.add(filler7);

        btnConfirmar.setText("Confirmar");
        panel.add(btnConfirmar);

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JPanel filaAvatares;
    private javax.swing.JPanel filaColores;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblAyudaColor;
    private javax.swing.JLabel lblColor;
    private javax.swing.JLabel lblErrorNombre;
    private javax.swing.JLabel lblMsgAvatar;
    private javax.swing.JLabel lblMsgColor;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panel;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
    private static class Circulo extends JComponent {

        private final java.awt.Color relleno;
        private final String texto;
        private boolean seleccionado, atenuado;

        Circulo(java.awt.Color relleno, String texto, Runnable alClick) {
            this.relleno = relleno;
            this.texto = texto;
            setPreferredSize(new Dimension(40, 40));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!atenuado) {
                        alClick.run();
                    }
                }
            });
        }

        void setSeleccionado(boolean s) {
            seleccionado = s;
            repaint();
        }

        void setAtenuado(boolean a) {
            atenuado = a;
            setCursor(Cursor.getPredefinedCursor(a ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
            repaint();
        }

        private static java.awt.Color aclarar(java.awt.Color c, float f) {
            return new java.awt.Color(
                    (int) (c.getRed() + (255 - c.getRed()) * f),
                    (int) (c.getGreen() + (255 - c.getGreen()) * f),
                    (int) (c.getBlue() + (255 - c.getBlue()) * f));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(atenuado ? aclarar(relleno, 0.75f) : relleno);
            g2.fillOval(4, 4, 32, 32);
            if (seleccionado) {
                g2.setColor(AZUL_OSCURO);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(2, 2, 36, 36);
            }
            if (texto != null) {
                g2.setColor(AZUL_OSCURO);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(texto, (40 - fm.stringWidth(texto)) / 2, (40 + fm.getAscent() - fm.getDescent()) / 2);
            }
            g2.dispose();
        }
    }
}
