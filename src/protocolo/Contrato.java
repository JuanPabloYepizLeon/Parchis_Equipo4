package protocolo;

/**
 * Nombres que el cliente y el servidor usan para comunicarse: servicios, acciones, campos,
 * eventos y errores. Así ambos lados hablan el mismo "idioma".
 */
public final class Contrato {

    public static final int VERSION = Sobre.VERSION;

    private Contrato() {
    }

    /** Nombres de los servicios. */
    public static final class Servicios {

        public static final String SISTEMA = "sistema";
        public static final String PARTIDA = "partida";
        public static final String JUGADOR = "jugador";
        public static final String TURNO = "turno";

        private Servicios() {
        }
    }

    /** Nombres de las acciones de cada servicio. */
    public static final class Acciones {

        // sistema
        public static final String PING = "ping";
        public static final String DESCRIBIR = "describir";
        // partida
        public static final String CREAR = "crear";
        public static final String UNIRSE = "unirse";
        public static final String INICIAR = "iniciar";
        public static final String ESTADO = "estado";
        // jugador
        public static final String REGISTRAR = "registrar";
        public static final String LISTO = "listo";
        public static final String ABANDONAR = "abandonar";
        public static final String REANUDAR = "reanudar";
        // turno
        public static final String LANZAR_DADO = "lanzarDado";
        public static final String MOVER_FICHA = "moverFicha";

        private Acciones() {
        }
    }

    /** Nombres de los datos que se envían en peticiones y respuestas. */
    public static final class Campos {

        public static final String MAX_JUGADORES = "maxJugadores";
        public static final String NOMBRE = "nombre";
        public static final String COLOR = "color";
        public static final String AVATAR = "avatar";
        public static final String LISTO = "listo";
        public static final String ID_FICHA = "idFicha";
        public static final String ANFITRION = "anfitrion";
        public static final String MI_COLOR = "miColor";
        public static final String VALOR = "valor";
        public static final String PIERDE_TURNO = "pierdeTurno";
        public static final String MOVIBLES = "movibles";
        public static final String DESDE = "desde";
        public static final String HASTA = "hasta";
        public static final String CAPTURADAS = "capturadas";
        public static final String LLEGO_A_META = "llegoAMeta";
        public static final String BONIFICACION = "bonificacion";
        public static final String VERSION = "version";
        public static final String SERVICIOS = "servicios";

        private Campos() {
        }
    }

    /** Nombres de los datos de la copia de la partida. */
    public static final class Snapshot {

        public static final String ESTADO = "estado";
        public static final String MAX_JUGADORES = "maxJugadores";
        public static final String TODOS_LISTOS = "todosListos";
        public static final String TURNO = "turno";
        public static final String FASE = "fase";
        public static final String PASOS_PENDIENTES = "pasosPendientes";
        public static final String ULTIMO_DADO = "ultimoDado";
        public static final String GANADOR = "ganador";
        public static final String MOTIVO_FIN = "motivoFin";
        public static final String COLOR_ANFITRION = "colorAnfitrion";
        public static final String SEIS_CONSECUTIVOS = "seisConsecutivos";
        public static final String TIRO_EXTRA = "tiroExtra";
        public static final String ULTIMO_JUGADOR = "ultimoJugador";
        public static final String MOVIBLES = "movibles";
        public static final String JUGADORES = "jugadores";
        // de cada jugador
        public static final String NOMBRE = "nombre";
        public static final String COLOR = "color";
        public static final String AVATAR = "avatar";
        public static final String ESTADO_JUGADOR = "estado";
        public static final String DIRECCION = "direccion";
        public static final String FICHAS_EN_META = "fichasEnMeta";
        public static final String FICHAS = "fichas";
        // de cada ficha
        public static final String ID = "id";
        public static final String ESTADO_FICHA = "estado";
        public static final String POSICION = "posicion";

        private Snapshot() {
        }
    }

    /** Avisos que el servidor manda cuando la partida cambia. */
    public static final class Eventos {

        public static final String JUGADOR_REGISTRADO = "JUGADOR_REGISTRADO";
        public static final String ESTADO_JUGADOR_CAMBIADO = "ESTADO_JUGADOR_CAMBIADO";
        public static final String PARTIDA_INICIADA = "PARTIDA_INICIADA";
        public static final String DADO_LANZADO = "DADO_LANZADO";
        public static final String FICHA_MOVIDA = "FICHA_MOVIDA";
        public static final String FICHA_CAPTURADA = "FICHA_CAPTURADA";
        public static final String FICHA_EN_META = "FICHA_EN_META";
        public static final String TURNO_PERDIDO = "TURNO_PERDIDO";
        public static final String TURNO_CAMBIADO = "TURNO_CAMBIADO";
        public static final String PARTIDA_TERMINADA = "PARTIDA_TERMINADA";
        public static final String JUGADOR_ABANDONO = "JUGADOR_ABANDONO";
        /** Evento interno del servicio sistema para mantener viva la conexión. */
        public static final String LATIDO = "LATIDO";

        private Eventos() {
        }
    }

    /** Tipos de error que puede devolver el servidor. */
    public static final class Errores {

        public static final String NEGOCIO = "NEGOCIO";
        public static final String VALIDACION = "VALIDACION";
        public static final String INTERNO = "INTERNO";
        public static final String JSON_INVALIDO = "JSON_INVALIDO";
        public static final String SOBRE_INVALIDO = "SOBRE_INVALIDO";
        public static final String SERVICIO_DESCONOCIDO = "SERVICIO_DESCONOCIDO";
        public static final String ACCION_DESCONOCIDA = "ACCION_DESCONOCIDA";

        private Errores() {
        }
    }
}
