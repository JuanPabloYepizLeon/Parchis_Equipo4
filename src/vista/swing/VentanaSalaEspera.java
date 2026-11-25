package vista.swing;

import cliente.PartidaVista.JugadorVista;
import controlador.IniciarPartidaController;
import controlador.SolicitudInicioController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import modelo.EstadoJugador;
import vista.VistaSalaEspera;

/**
 * Ventana de la sala de espera: lista de jugadores y botones de listo e iniciar.
 */
public class VentanaSalaEspera extends javax.swing.JFrame implements VistaSalaEspera {

    private static final int ANCHO = 340;
    private static final Color GRIS_CHIP = new Color(0xE5, 0xE7, 0xEB);
    private static final Color GRIS_DESHAB = new Color(0xD1, 0xD5, 0xDB);
    private static final Color VERDE_FONDO = new Color(0xD1, 0xFA, 0xE5);
    private static final Color VERDE_TEXTO = new Color(0x06, 0x5F, 0x46);
    private static final int ALTO_FILA = 44;
    
    private final modelo.Color colorLocal;

    private EstadoJugador estadoLocal = EstadoJugador.ESPERANDO;
    private SolicitudInicioController cu5;
    private IniciarPartidaController cu3;

    public VentanaSalaEspera(modelo.Color colorLocal, boolean esAnfitrion) {
        initComponents();
        this.colorLocal = colorLocal;

        Estilos.secundario(btnListo);
        Estilos.anchoCompleto(btnListo, ANCHO);
        btnListo.addActionListener(e -> {
            if (estadoLocal == EstadoJugador.LISTO) {
                cu5.cancelarListo();
            } else {
                cu5.solicitarInicio();
            }
        });
        
        Estilos.primario(btnIniciar);
        Estilos.anchoCompleto(btnIniciar, ANCHO);
        btnIniciar.addActionListener(e -> cu3.iniciarPartida());
        habilitarIniciar(false);
        
        lblAyuda.setText(esAnfitrion
                ? "Solo Anfitrión - se habilita cuando todos estén listos."
                : "Espera a que el anfitrión inicie la partida.");
        
        if (!esAnfitrion) {
            panel.remove(fillerIniciar);
            panel.remove(btnIniciar);
        }
        
        setResizable(false);
        pack();
    }
    
    public void setControladores(SolicitudInicioController cu5, IniciarPartidaController cu3) {
        this.cu5 = cu5;
        this.cu3 = cu3;
    }
    
    public void mostrar() {
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    @Override
    public void mostrarJugadores(List<JugadorVista> jugadores) {
        listaJugadores.removeAll();
        for (JugadorVista j : jugadores) {
            listaJugadores.add(fila(j));
        }
        listaJugadores.revalidate();
        listaJugadores.repaint();
    }
    
    @Override
    public void mostrarEstadoLocal(EstadoJugador estado) {
        estadoLocal = estado;
        btnListo.setText(estado == EstadoJugador.LISTO ? "Quitar Listo" : "Marcar como Listo");
    }
    
    @Override
    public void habilitarIniciar(boolean habilitado) {
        btnIniciar.setEnabled(habilitado);
        btnIniciar.setBackground(habilitado ? Estilos.AZUL_OSCURO : GRIS_DESHAB);
    }

    @Override
    public void mostrarModal(String titulo, String mensaje) {
        Estilos.mostrarModal(this, titulo, mensaje);
    }

    private JPanel fila(JugadorVista j) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_FILA));
        fila.setPreferredSize(new Dimension(ANCHO, ALTO_FILA));
        fila.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Estilos.GRIS_BORDE));

        Color colorJugador = Paleta.awt(j.color());
        JComponent punto = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorJugador);
                g2.fillOval(0, 0, 14, 14);
                g2.dispose();
            }
        };
        punto.setPreferredSize(new Dimension(14, 14));
        
        boolean esLocal = j.color() == colorLocal;
        JLabel nombre = new JLabel(j.nombre() + (esLocal ? " (Tú)" : ""));
        nombre.setFont(new Font("SansSerif", esLocal ? Font.BOLD : Font.PLAIN, 13));
        
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 14));
        izquierda.setOpaque(false);
        izquierda.add(punto);
        izquierda.add(nombre);
        
        boolean listo = j.estado() == EstadoJugador.LISTO;
        JLabel chip = new JLabel(listo ? "Listo" : "Esperando");
        chip.setOpaque(true);
        chip.setFont(new Font("SansSerif", Font.PLAIN, 11));
        chip.setBackground(listo ? VERDE_FONDO : GRIS_CHIP);
        chip.setForeground(listo ? VERDE_TEXTO : Color.DARK_GRAY);
        chip.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        derecha.setOpaque(false);
        derecha.add(chip);
        
        fila.add(izquierda, BorderLayout.WEST);
        fila.add(derecha, BorderLayout.EAST);
        return fila;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 16), new java.awt.Dimension(0, 16), new java.awt.Dimension(32767, 16));
        listaJugadores = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 12), new java.awt.Dimension(0, 12), new java.awt.Dimension(32767, 12));
        btnListo = new javax.swing.JButton();
        fillerIniciar = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        btnIniciar = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        lblAyuda = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sala de Espera");
        setResizable(false);

        panel.setBackground(new java.awt.Color(255, 255, 255));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 32, 24, 32));
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));

        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        lblTitulo.setText("Sala de Espera");
        lblTitulo.setToolTipText("");
        panel.add(lblTitulo);
        panel.add(filler1);

        listaJugadores.setMaximumSize(new java.awt.Dimension(2147483647, 176));
        listaJugadores.setMinimumSize(new java.awt.Dimension(340, 176));
        listaJugadores.setOpaque(false);
        listaJugadores.setPreferredSize(new java.awt.Dimension(340, 176));
        listaJugadores.setLayout(new javax.swing.BoxLayout(listaJugadores, javax.swing.BoxLayout.Y_AXIS));
        panel.add(listaJugadores);
        panel.add(filler2);

        btnListo.setText("Marcar como Listo");
        panel.add(btnListo);
        panel.add(fillerIniciar);

        btnIniciar.setText("Iniciar Partida");
        panel.add(btnIniciar);
        panel.add(filler3);

        lblAyuda.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblAyuda.setForeground(new java.awt.Color(128, 128, 128));
        panel.add(lblAyuda);

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnListo;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler fillerIniciar;
    private javax.swing.JLabel lblAyuda;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel listaJugadores;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
