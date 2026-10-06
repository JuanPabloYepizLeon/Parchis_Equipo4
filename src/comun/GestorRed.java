package comun;

import modelo.Evento;
import modelo.Partida;

public interface GestorRed {

    void abrirServidor(int puerto)
            throws ErrorNegocioException;

    Partida solicitarAcceso(String direccion, int puerto)
            throws ErrorNegocioException;

    void difundir(Evento evento, Partida partida);
}
