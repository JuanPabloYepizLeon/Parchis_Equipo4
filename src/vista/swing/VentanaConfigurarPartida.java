package vista.swing;

import controlador.ConfigurarPartidaController;
import vista.VistaConfigurarPartida;

/**
 * Ventana para crear una partida: puerto y cantidad de jugadores.
 */
public class VentanaConfigurarPartida extends javax.swing.JFrame implements VistaConfigurarPartida {

    private ConfigurarPartidaController controlador;

    public VentanaConfigurarPartida() {
        initComponents();

        Estilos.primario(btnCrear);
        Estilos.anchoCompleto(btnCrear, 300);
        btnCrear.addActionListener(e -> crear());
        getRootPane().setDefaultButton(btnCrear);
        
        setResizable(false);
        pack();
    }
    
    public void setControlador(ConfigurarPartidaController controlador) {
        this.controlador = controlador;
    }
    
    public void mostrar() {
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void crear() {
        campoPuerto.limpiarError();
        campoMax.limpiarError();
        controlador.crearPartida(campoPuerto.texto(), campoMax.texto());
    }
    
    @Override
    public void marcarCampoError(String campo, String mensaje) {
        switch (campo) {
            case "puerto" ->
                campoPuerto.error(mensaje);
            case "maxJugadores" ->
                campoMax.error(mensaje);
            default ->
                mostrarModal("Error", mensaje);
        }
    }
    
    @Override
    public void mostrarModal(String titulo, String mensaje) {
        Estilos.mostrarModal(this, titulo, mensaje);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        campoPuerto = new vista.swing.Campo();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        campoMax = new vista.swing.Campo();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 16), new java.awt.Dimension(0, 16), new java.awt.Dimension(32767, 16));
        btnCrear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configurar Partida");
        setResizable(false);

        panel.setBackground(new java.awt.Color(255, 255, 255));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 32, 24, 32));
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));

        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        lblTitulo.setText("Configurar Partida");
        panel.add(lblTitulo);
        panel.add(filler1);

        campoPuerto.setEtiqueta("Puerto");
        panel.add(campoPuerto);
        panel.add(filler2);

        campoMax.setEtiqueta("Máximo de jugadores (2-4)");
        panel.add(campoMax);
        panel.add(filler3);

        btnCrear.setText("Crear partida");
        panel.add(btnCrear);

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCrear;
    private vista.swing.Campo campoMax;
    private vista.swing.Campo campoPuerto;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
