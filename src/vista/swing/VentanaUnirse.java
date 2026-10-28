package vista.swing;

import controlador.SolicitarUnirseController;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import vista.VistaUnirse;

public class VentanaUnirse extends javax.swing.JFrame implements VistaUnirse {

    private static final Color AZUL_OSCURO = new Color(0x2F, 0x34, 0x4A);

    private SolicitarUnirseController controlador;

    public VentanaUnirse() {
        initComponents();

        estilizarBoton(btnUnirse);
        btnUnirse.addActionListener(e -> unirse());
        getRootPane().setDefaultButton(btnUnirse);

        setResizable(false);
        pack();
    }

    public void setControlador(SolicitarUnirseController c) {
        this.controlador = c;
    }

    public void mostrar() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void unirse() {
        campoDireccion.limpiarError();
        campoPuerto.limpiarError();
        controlador.solicitarUnirse(campoDireccion.texto(), campoPuerto.texto());
    }

    @Override
    public void marcarCampoError(String campo, String mensaje) {
        switch (campo) {
            case "direccion" ->
                campoDireccion.error(mensaje);
            case "puerto" ->
                campoPuerto.error(mensaje);
            default ->
                mostrarModal("Error", mensaje);
        }
    }

    @Override
    public void mostrarModal(String titulo, String mensaje) {
        JDialog d = new JDialog(this, titulo, true);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblMensaje = new JLabel("<html><body style='width:240px'>" + mensaje + "</body></html>");
        lblMensaje.setAlignmentX(LEFT_ALIGNMENT);

        JButton aceptar = new JButton("Aceptar");
        estilizarBoton(aceptar);
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
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void estilizarBoton(JButton b) {
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setPreferredSize(new Dimension(300, 40));
        b.setBackground(AZUL_OSCURO);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        campoDireccion = new vista.swing.Campo();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        campoPuerto = new vista.swing.Campo();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 16), new java.awt.Dimension(0, 16), new java.awt.Dimension(32767, 16));
        btnUnirse = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Unirse a Partida");
        setResizable(false);

        panel.setBackground(new java.awt.Color(255, 255, 255));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 32, 24, 32));
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));

        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        lblTitulo.setText("Unirse a Partida");
        panel.add(lblTitulo);
        panel.add(filler1);

        campoDireccion.setEtiqueta("Dirección del servidor");
        panel.add(campoDireccion);
        panel.add(filler2);

        campoPuerto.setEtiqueta("Puerto");
        panel.add(campoPuerto);
        panel.add(filler3);

        btnUnirse.setText("Unirse");
        panel.add(btnUnirse);

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUnirse;
    private vista.swing.Campo campoDireccion;
    private vista.swing.Campo campoPuerto;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
