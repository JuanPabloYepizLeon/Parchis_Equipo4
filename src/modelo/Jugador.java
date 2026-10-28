package modelo;

import java.util.ArrayList;
import java.util.List;

public class Jugador {

    public static final int FICHAS_POR_JUGADOR = 4;

    private final String nombre;
    private final Color color;
    private final String avatar;
    private EstadoJugador estado = EstadoJugador.ESPERANDO;
    private final List<Ficha> fichas = new ArrayList<>();

    public Jugador(String nombre, Color color, String avatar) {
        this.nombre = nombre;
        this.color = color;
        this.avatar = avatar;
        for (int id = 1; id <= FICHAS_POR_JUGADOR; id++) {
            fichas.add(new Ficha(id, color));
        }
    }

    public void cambiarEstado(EstadoJugador estado) {
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public Color getColor() {
        return color;
    }

    public String getAvatar() {
        return avatar;
    }

    public EstadoJugador getEstado() {
        return estado;
    }

    public List<Ficha> getFichas() {
        return List.copyOf(fichas);
    }

    public Ficha getFicha(int id) {
        for (Ficha f : fichas) {
            if (f.getId() == id) {
                return f;
            }
        }
        return null;
    }

    public int fichasEnMeta() {
        int n = 0;
        for (Ficha f : fichas) {
            if (f.getEstado() == EstadoFicha.EN_META) {
                n++;
            }
        }
        return n;
    }

}
