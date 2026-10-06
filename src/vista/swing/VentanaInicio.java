package vista.swing;

import java.awt.Color;
import javax.swing.BorderFactory;

public class VentanaInicio extends javax.swing.JFrame {

    private static final Color AZUL_OSCURO = new Color(0x2F, 0x34, 0x4A);

    public VentanaInicio(String titulo, Runnable alCrear, Runnable alUnirse) {
        initComponents();
        setTitle(titulo);

        estilizarPrimario(btnCrear);
        estilizarSecundario(btnUnirse);

        btnCrear.addActionListener(e -> alCrear.run());
        btnUnirse.addActionListener(e -> alUnirse.run());

        setResizable(false);
        pack();
    }

    public void mostrar() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void ocultar() {
        setVisible(false);
    }

    private void estilizarPrimario(javax.swing.JButton b) {
        b.setBackground(AZUL_OSCURO);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder());
    }

    private void estilizarSecundario(javax.swing.JButton b) {
        b.setBackground(Color.WHITE);
        b.setForeground(AZUL_OSCURO);
        b.setBorder(BorderFactory.createLineBorder(AZUL_OSCURO, 1));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tarjeta = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 4), new java.awt.Dimension(0, 4), new java.awt.Dimension(32767, 4));
        lblSub = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28), new java.awt.Dimension(0, 28), new java.awt.Dimension(32767, 28));
        btnCrear = new javax.swing.JButton();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 12), new java.awt.Dimension(0, 12), new java.awt.Dimension(32767, 12));
        btnUnirse = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        tarjeta.setBackground(new java.awt.Color(255, 255, 255));
        tarjeta.setBorder(javax.swing.BorderFactory.createEmptyBorder(32, 40, 32, 40));
        tarjeta.setLayout(new javax.swing.BoxLayout(tarjeta, javax.swing.BoxLayout.Y_AXIS));

        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 32)); // NOI18N
        lblTitulo.setText("Parchís");
        lblTitulo.setAlignmentX(0.5F);
        tarjeta.add(lblTitulo);
        tarjeta.add(filler4);

        lblSub.setForeground(new java.awt.Color(128, 128, 128));
        lblSub.setText("Juego multijugador");
        lblSub.setAlignmentX(0.5F);
        tarjeta.add(lblSub);
        tarjeta.add(filler5);

        btnCrear.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnCrear.setText("Crear Partida");
        btnCrear.setAlignmentX(0.5F);
        btnCrear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCrear.setFocusPainted(false);
        btnCrear.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        btnCrear.setOpaque(true);
        btnCrear.setPreferredSize(new java.awt.Dimension(260, 40));
        tarjeta.add(btnCrear);
        tarjeta.add(filler6);

        btnUnirse.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnUnirse.setText("Unirse a Partida");
        btnUnirse.setAlignmentX(0.5F);
        btnUnirse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUnirse.setFocusPainted(false);
        btnUnirse.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        btnUnirse.setOpaque(true);
        btnUnirse.setPreferredSize(new java.awt.Dimension(260, 40));
        tarjeta.add(btnUnirse);

        getContentPane().add(tarjeta, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCrear;
    private javax.swing.JButton btnUnirse;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JLabel lblSub;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel tarjeta;
    // End of variables declaration//GEN-END:variables
}
