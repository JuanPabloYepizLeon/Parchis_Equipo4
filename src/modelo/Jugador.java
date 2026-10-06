package modelo;

public class Jugador {

    private final String nombre;
    private final Color color;
    private final String avatar;
    private EstadoJugador estado = EstadoJugador.ESPERANDO;

    public Jugador(String nombre, Color color, String avatar) {
        this.nombre = nombre;
        this.color = color;
        this.avatar = avatar;
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

}
