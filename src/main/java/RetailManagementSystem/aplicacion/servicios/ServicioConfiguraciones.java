package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.dto.consultas.ConfiguracionSistemaDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.PoliticaDeBloqueoDTO;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioConfiguracion;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

public class ServicioConfiguraciones {

    //ATRIBUTOS:

    private static final String CONF_DATOS_TIENDA = "NOMBRE_PROYECTO_PROPIO_ORIGINAL";

    private static final String CONF_MAX_INTENTOS = "SEGURIDAD_MAX_INTENTOS";

    private static final String CONF_MINUTOS_BLOQUEO = "SEGURIDAD_MINUTOS_BLOQUEO";

    private final RepositorioConfiguracion repositorioConfiguracion;

    private final ProveedorConfiguracion proveedorConfiguracion;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioConfiguraciones(
            RepositorioConfiguracion repositorioConfiguracion, ProveedorConfiguracion proveedorConfiguracion,
            GestorTransaccional gestorTransaccional
    ) {
        this.repositorioConfiguracion = repositorioConfiguracion;
        this.proveedorConfiguracion = proveedorConfiguracion;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public String obtenerValorConfiguracion(String clave){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioConfiguracion.obtenerValorConfiguracion(clave)
        );
    }

    public ConfiguracionSistemaDTO obtenerConfiguracionSistema(String clave){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioConfiguracion.obtenerConfiguracionSistema(clave)
        );
    }

    private void actualizarValorConfiguracion(String clave, String valor){
        this.repositorioConfiguracion.actualizarValorConfiguracion(clave, valor);
    }

    //MÉTODOS ESPECÍFICOS:

    public String obtenerNombreTienda(){
        return obtenerValorConfiguracion(CONF_DATOS_TIENDA);
    }

    public ConfiguracionSistemaDTO obtenerDatosTienda(){
        return obtenerConfiguracionSistema(CONF_DATOS_TIENDA);
    }

    public void cambiarNombreYDescripcionTienda(String nombreNuevo, String descripcion) {
        if (nombreNuevo == null || nombreNuevo.isBlank()){
            throw new IllegalArgumentException("El Nombre de la Tienda NO puede estar Vacío.");
        }
        if (descripcion == null || descripcion.isBlank()){
            throw new IllegalArgumentException("La Description NO puede estar Vacía.");
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                this.repositorioConfiguracion.actualizarConfiguracionSistemaConfiguracion(
                        CONF_DATOS_TIENDA, nombreNuevo.trim(), descripcion.trim()
                )
        );
        this.proveedorConfiguracion.invalidarCache(CONF_DATOS_TIENDA);
    }

    public PoliticaDeBloqueoDTO obtenerPoliticaDeBloqueo(){
        ConfiguracionSistemaDTO maxIntentos = obtenerConfiguracionSistema(CONF_MAX_INTENTOS);
        ConfiguracionSistemaDTO minutosBloqueo = obtenerConfiguracionSistema(CONF_MINUTOS_BLOQUEO);
        return new PoliticaDeBloqueoDTO(maxIntentos, minutosBloqueo);
    }

    public void actualizarMaxIntentos(int nuevoMaximo) {
        if (nuevoMaximo <= 0){
            throw new IllegalArgumentException("Los Intentos Máximos son Inválidos");
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                actualizarValorConfiguracion(
                        CONF_MAX_INTENTOS, String.valueOf(nuevoMaximo)
                )
        );
        this.proveedorConfiguracion.invalidarCache(CONF_MAX_INTENTOS);
    }

    public void actualizarMaxMinutosBloqueos(int nuevosMinutosBloqueo) {
        if (nuevosMinutosBloqueo <= 0){
            throw new IllegalArgumentException("Los Minutos de Bloqueo son Inválidos");
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                actualizarValorConfiguracion(
                        CONF_MINUTOS_BLOQUEO, String.valueOf(nuevosMinutosBloqueo)
                )
        );
        this.proveedorConfiguracion.invalidarCache(CONF_MINUTOS_BLOQUEO);
    }

}//===================================================================================================================//

