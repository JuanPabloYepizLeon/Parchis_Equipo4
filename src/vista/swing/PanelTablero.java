package vista.swing;

import cliente.PartidaVista;
import cliente.PartidaVista.FichaVista;
import cliente.PartidaVista.JugadorVista;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Dibuja el tablero y las fichas, y deja elegir con un clic la ficha que se quiere mover.
 */
public class PanelTablero extends JPanel {

    /** Recibe el número de la ficha a la que el jugador le dio clic. */
    public interface OyenteFicha {

        void fichaElegida(int idFicha);
    }

    private static final String RECURSO = "recursos/tablero.png";
    private static final double RADIO_FICHA = 10;
    private static final double DESPLAZAMIENTO_APILADA = 6;
    private static final Color DORADO = new Color(0xFF, 0xD7, 0x00);

    private final BufferedImage imagen = cargarImagen();
    private final List<Hueco> huecos = new ArrayList<>();

    private PartidaVista partida = PartidaVista.VACIA;
    private modelo.Color miColor;
    private Set<Integer> movibles = new HashSet<>();
    private OyenteFicha oyente;

    public PanelTablero() {
        setPreferredSize(new Dimension(600, 600));
        // Sin fondo propio: alrededor de la imagen se ve el fondo de la ventana, no un margen blanco.
        setOpaque(false);
        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Hueco h = huecoEn(e);
                if (h != null && oyente != null) {
                    oyente.fichaElegida(h.idFicha);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(huecoEn(e) == null ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    public void setOyente(OyenteFicha oyente) {
        this.oyente = oyente;
    }

    public void actualizar(PartidaVista partida, modelo.Color miColor) {
        this.partida = partida;
        this.miColor = miColor;
        this.movibles = new HashSet<>();
        repaint();
    }

    public void setMovibles(Collection<Integer> ids) {
        this.movibles = new HashSet<>(ids);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.translate(desplazamientoX(), desplazamientoY());
        g.scale(escala(), escala());
        if (imagen == null) {
            g.setColor(Color.DARK_GRAY);
            g.drawString("No se encontró " + RECURSO, 20, 30);
        } else {
            g.drawImage(imagen, 0, 0, (int) CoordenadasTablero.TAM, (int) CoordenadasTablero.TAM, null);
        }
        pintarFichas(g);
        g.dispose();
    }

    private void pintarFichas(Graphics2D g) {
        huecos.clear();
        Map<String, Integer> apiladas = new HashMap<>();
        for (JugadorVista jugador : partida.jugadores()) {
            modelo.Color color = jugador.color();
            for (FichaVista ficha : jugador.fichas()) {
                int id = ficha.id();
                Point2D.Double p = CoordenadasTablero.de(color, id, ficha.posicion());
                int encima = apiladas.merge(Math.round(p.x) + "," + Math.round(p.y), 1, Integer::sum) - 1;
                double x = p.x + DESPLAZAMIENTO_APILADA * encima;
                double y = p.y - DESPLAZAMIENTO_APILADA * encima;
                boolean seleccionable = color == miColor && movibles.contains(id);
                pintarFicha(g, x, y, Paleta.awt(color), id, seleccionable);
                if (seleccionable) {
                    huecos.add(new Hueco(new Ellipse2D.Double(x - RADIO_FICHA - 2, y - RADIO_FICHA - 2,
                            2 * RADIO_FICHA + 4, 2 * RADIO_FICHA + 4), id));
                }
            }
        }
    }

    private void pintarFicha(Graphics2D g, double x, double y, Color relleno, int id, boolean seleccionable) {
        if (seleccionable) {
            g.setColor(DORADO);
            g.setStroke(new BasicStroke(3.5f));
            g.draw(new Ellipse2D.Double(x - RADIO_FICHA - 2, y - RADIO_FICHA - 2, 2 * RADIO_FICHA + 4, 2 * RADIO_FICHA + 4));
        }
        g.setColor(relleno);
        g.fill(new Ellipse2D.Double(x - RADIO_FICHA, y - RADIO_FICHA, 2 * RADIO_FICHA, 2 * RADIO_FICHA));
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new Ellipse2D.Double(x - RADIO_FICHA, y - RADIO_FICHA, 2 * RADIO_FICHA, 2 * RADIO_FICHA));
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        String texto = String.valueOf(id);
        g.drawString(texto, (float) (x - fm.stringWidth(texto) / 2.0), (float) (y + fm.getAscent() / 2.0 - 1));
    }

    private Hueco huecoEn(MouseEvent e) {
        double ix = (e.getX() - desplazamientoX()) / escala();
        double iy = (e.getY() - desplazamientoY()) / escala();
        for (int i = huecos.size() - 1; i >= 0; i--) {
            if (huecos.get(i).zona.contains(ix, iy)) {
                return huecos.get(i);
            }
        }
        return null;
    }

    private double escala() {
        return Math.max(1, Math.min(getWidth(), getHeight())) / CoordenadasTablero.TAM;
    }

    private double desplazamientoX() {
        return (getWidth() - CoordenadasTablero.TAM * escala()) / 2;
    }

    private double desplazamientoY() {
        return (getHeight() - CoordenadasTablero.TAM * escala()) / 2;
    }

    private static BufferedImage cargarImagen() {
        URL url = PanelTablero.class.getResource(RECURSO);
        if (url == null) {
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException ex) {
            return null;
        }
    }

    /** Zona de la pantalla donde se puede hacer clic sobre una ficha. */
    private record Hueco(Ellipse2D zona, int idFicha) {

    }
}
