package servidor.servicios;

import java.util.LinkedHashMap;
import java.util.Map;
import protocolo.Contrato;
import protocolo.Contrato.Acciones;
import protocolo.Contrato.Campos;
import protocolo.Contrato.Servicios;
import servidor.EnrutadorServicios;
import servidor.ServicioBase;

/**
 * Servicio "sistema": comprobar que la conexión sigue viva y describir los servicios disponibles.
 */
public final class ServicioSistema extends ServicioBase {

    public ServicioSistema(EnrutadorServicios enrutador) {
        super(Servicios.SISTEMA);
        registrar(Acciones.PING, (p, c) -> Map.of());
        registrar(Acciones.DESCRIBIR, (p, c) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put(Campos.VERSION, Contrato.VERSION);
            m.put(Campos.SERVICIOS, enrutador.catalogo());
            return m;
        });
    }
}
