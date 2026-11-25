package vista.swing;

import cliente.PartidaVista;
import cliente.PartidaVista.JugadorVista;
import controlador.AbandonarPartidaController;
import controlador.EjercerTurnoRedController;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import modelo.Color;
import modelo.EstadoPartida;
import modelo.FaseTurno;
import vista.VistaAbandonarPartida;
import vista.VistaTableroRed;

/**
 * Ventana del juego: tablero, turno, dado, jugadores y botón para abandonar.
 */
public class VentanaTablero extends javax.swing.JFrame implements VistaTableroRed, VistaAbandonarPartida {

    private EjercerTurnoRedController controlador;
    private AbandonarPartidaController controladorAbandono;

    public VentanaTablero() {
        initComponents();
        panelTablero.setOyente(id -> controlador.moverFicha(id));
        panelLateral.setBorder(BorderFactory.createEmptyBorder(6, 28, 10, 12));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(14, 0, 6, 0));
        lblMensaje.setFont(lblMensaje.getFont().deriveFont(java.awt.Font.BOLD, 13f));
    }

    public void setControladorAbandono(AbandonarPartidaController c) {
        this.controladorAbandono = c;
    }

    private void confirmarAbandono() {
        Object[] opciones = {"Cancelar", "Abandonar"};
        int r = JOptionPane.showOptionDialog(this,
                "Esta acción es irreversible: no podrás volver a la partida y tus fichas serán retiradas del tablero.",
                "¿Abandonar la partida?", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                opciones, opciones[0]);
        if (r == 1) {
            controladorAbandono.abandonar();
        }
    }

    public void setControlador(EjercerTurnoRedController c) {
        this.controlador = c;
    }

    public void mostrar() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void mostrarPartida(PartidaVista partida, Color miColor) {
        panelTablero.actualizar(partida, miColor);
        barraFichas.removeAll();

        Color turno = partida.turno();
        boolean miTurno = turno != null && turno == miColor;
        lblTurno.setText("Turno de: " + (turno == null ? "-" : partida.nombreDe(turno)) + (miTurno ? " (tú)" : ""));
        int dado = partida.ultimoDado();
        lblDado.setText("Dado: " + (dado == 0 ? "-" : dado));
        btnLanzar.setEnabled(miTurno && partida.estado() == EstadoPartida.EN_JUEGO
                && partida.fase() == FaseTurno.ESPERANDO_LANZAMIENTO);

        panelJugadores.removeAll();
        for (JugadorVista j : partida.jugadores()) {
            boolean esTurno = j.color() == turno;
            boolean abandono = j.abandono();
            JLabel l = new JLabel("<html>"
                    + (esTurno ? "<b>" : "") + (abandono ? "<s>" : "") + j.nombre()
                    + (abandono ? "</s>" : "") + (esTurno ? " &larr; turno</b>" : "")
                    + (abandono ? "<br>Abandonó la partida"
                    : "<br>En meta: " + j.fichasEnMeta() + "/4") + "</html>");
            l.setIcon(new IconoAvatar(Paleta.awt(j.color()), j.avatar(), esTurno, abandono));
            l.setIconTextGap(10);
            l.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            panelJugadores.add(l);
        }
        panelJugadores.revalidate();
        panelJugadores.repaint();
        barraFichas.revalidate();
        barraFichas.repaint();
    }

    @Override
    public void mostrarFichasMovibles(List<Integer> ids, int pasos) {
        btnLanzar.setEnabled(false);
        panelTablero.setMovibles(ids);
        barraFichas.removeAll();
        for (int id : ids) {
            JButton b = new JButton("Ficha " + id);
            b.addActionListener(e -> controlador.moverFicha(id));
            barraFichas.add(b);
        }
        barraFichas.revalidate();
        barraFichas.repaint();
        mostrarMensaje("Mueve " + pasos + " casillas: elige una ficha (botón o clic sobre ella).");
    }

    @Override
    public void mostrarMensaje(String texto) {
        lblMensaje.setText(texto == null || texto.isBlank() ? ""
                : "<html><div style='width:170px'>" + texto + "</div></html>");
    }

    @Override
    public void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void mostrarFinPartida(String texto) {
        JOptionPane.showMessageDialog(this, texto, "¡Partida terminada!", JOptionPane.INFORMATION_MESSAGE);
        controlador.volverAlInicio();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cabecera = new javax.swing.JPanel();
        lblTurno = new javax.swing.JLabel();
        lblDado = new javax.swing.JLabel();
        btnLanzar = new javax.swing.JButton();
        btnAbandonar = new javax.swing.JButton();
        panelLateral = new javax.swing.JPanel();
        panelJugadores = new javax.swing.JPanel();
        pie = new javax.swing.JPanel();
        barraFichas = new javax.swing.JPanel();
        lblMensaje = new javax.swing.JLabel();
        panelTablero = new vista.swing.PanelTablero();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Parchís - Tablero de Juego");

        cabecera.setPreferredSize(new java.awt.Dimension(50, 50));

        lblTurno.setFont(new java.awt.Font("SansSerif", 1, 15)); // NOI18N
        lblTurno.setText("Turno de: -");

        lblDado.setFont(new java.awt.Font("SansSerif", 1, 15)); // NOI18N
        lblDado.setText("Dado: -");

        btnLanzar.setText("Lanzar Dado");
        btnLanzar.setEnabled(false);
        btnLanzar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLanzarActionPerformed(evt);
            }
        });

        btnAbandonar.setText("Abandonar Partida");
        btnAbandonar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbandonarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cabeceraLayout = new javax.swing.GroupLayout(cabecera);
        cabecera.setLayout(cabeceraLayout);
        cabeceraLayout.setHorizontalGroup(
            cabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cabeceraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTurno)
                .addGap(60, 60, 60)
                .addComponent(lblDado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                .addComponent(btnAbandonar)
                .addGap(18, 18, 18)
                .addComponent(btnLanzar)
                .addGap(45, 45, 45))
        );
        cabeceraLayout.setVerticalGroup(
            cabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cabeceraLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(cabeceraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTurno)
                    .addComponent(lblDado)
                    .addComponent(btnLanzar)
                    .addComponent(btnAbandonar))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        getContentPane().add(cabecera, java.awt.BorderLayout.PAGE_START);

        panelLateral.setPreferredSize(new java.awt.Dimension(270, 0));
        panelLateral.setLayout(new java.awt.BorderLayout());

        panelJugadores.setLayout(new javax.swing.BoxLayout(panelJugadores, javax.swing.BoxLayout.Y_AXIS));
        panelLateral.add(panelJugadores, java.awt.BorderLayout.PAGE_START);

        pie.setLayout(new java.awt.BorderLayout());
        pie.add(lblMensaje, java.awt.BorderLayout.PAGE_START);

        barraFichas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        pie.add(barraFichas, java.awt.BorderLayout.CENTER);

        panelLateral.add(pie, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelLateral, java.awt.BorderLayout.LINE_END);

        javax.swing.GroupLayout panelTableroLayout = new javax.swing.GroupLayout(panelTablero);
        panelTablero.setLayout(panelTableroLayout);
        panelTableroLayout.setHorizontalGroup(
            panelTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );
        panelTableroLayout.setVerticalGroup(
            panelTableroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 615, Short.MAX_VALUE)
        );

        getContentPane().add(panelTablero, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLanzarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLanzarActionPerformed
        btnLanzar.setEnabled(false);
        controlador.lanzarDado();
    }//GEN-LAST:event_btnLanzarActionPerformed

    private void btnAbandonarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbandonarActionPerformed
        confirmarAbandono();
    }//GEN-LAST:event_btnAbandonarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel barraFichas;
    private javax.swing.JButton btnAbandonar;
    private javax.swing.JButton btnLanzar;
    private javax.swing.JPanel cabecera;
    private javax.swing.JLabel lblDado;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblTurno;
    private javax.swing.JPanel panelJugadores;
    private javax.swing.JPanel panelLateral;
    private vista.swing.PanelTablero panelTablero;
    private javax.swing.JPanel pie;
    // End of variables declaration//GEN-END:variables
}
