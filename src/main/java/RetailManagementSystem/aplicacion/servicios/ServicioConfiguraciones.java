package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.dto.consultas.ConfiguracionSistemaDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioConfiguracion;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

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

    public ConfiguracionSistemaDTO obtenerValorYDescripcion(String clave){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioConfiguracion.obtenerValorYDescripcion(clave)
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
        return obtenerValorYDescripcion(CONF_DATOS_TIENDA);
    }

    public void cambiarNombreYDescripcionTienda(
            UsuarioDTOCompleto usuario, String nombreNuevo, String descripcion
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.EDITAR_PERFIL_DE_TIENDA);
        if (nombreNuevo == null || nombreNuevo.isBlank()){
            throw new IllegalArgumentException("El Nombre de la Tienda NO puede estar Vacío.");
        }
        if (descripcion == null){
            throw new IllegalArgumentException("La Description NO puede ser Nula");
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                this.repositorioConfiguracion.actualizarValorYDescripcionConfiguracion(
                        CONF_DATOS_TIENDA, nombreNuevo, descripcion
                )
        );
        this.proveedorConfiguracion.invalidarCache(CONF_DATOS_TIENDA);
    }

    public ConfiguracionSistemaDTO obtenerMaxIntentosBloqueo(){
        return obtenerValorYDescripcion(CONF_MAX_INTENTOS);
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

    public ConfiguracionSistemaDTO obtenerMaxMinutosBloqueo(){
        return obtenerValorYDescripcion(CONF_MINUTOS_BLOQUEO);
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

