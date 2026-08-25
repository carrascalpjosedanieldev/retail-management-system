package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.puertos.RepositorioConfiguracion;
import RetailManagementSystem.infraestructura.configuracion.ProveedorConfiguracionImpl;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

public class ServicioConfiguraciones {

    //ATRIBUTOS:

    private static final String CONF_DATOS_TIENDA = "NOMBRE_PROYECTO_PROPIO_ORIGINAL";

    private static final String CONF_MAX_INTENTOS = "SEGURIDAD_MAX_INTENTOS";

    private static final String CONF_MINUTOS_BLOQUEO = "SEGURIDAD_MINUTOS_BLOQUEO";

    private final RepositorioConfiguracion repositorioConfiguracion;

    private final ProveedorConfiguracion proveedorConfiguracion;

    public ServicioConfiguraciones(RepositorioConfiguracion repositorioConfiguracion) {
        this.repositorioConfiguracion = repositorioConfiguracion;
        this.proveedorConfiguracion = new ProveedorConfiguracionImpl(repositorioConfiguracion);
    }

    public String obtenerValorConfiguracion(String clave){
        return this.repositorioConfiguracion.obtenerValorConfiguracion(clave);
    }

    public void actualizarValorConfiguracion(String clave, String valor){
        this.repositorioConfiguracion.actualizarValorConfiguracion(clave, valor);
    }

    public String obtenerDescripcionConfiguracion(String clave){
        return this.repositorioConfiguracion.obtenerDescripcionConfiguracion(clave);
    }

    public void actualizarDescripcionConfiguracion(String clave, String descripcion){
        this.repositorioConfiguracion.actualizarDescripcionConfiguracion(clave, descripcion);
    }

    //MÉTODOS ESPECÍFICOS:

    public String obtenerNombreTienda(){
        return this.proveedorConfiguracion.obtenerValorConfiguracion(CONF_DATOS_TIENDA);
    }

    public String obtenerDescripcionTienda(){
        return this.repositorioConfiguracion.obtenerDescripcionConfiguracion(CONF_DATOS_TIENDA);
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
        this.repositorioConfiguracion.actualizarValorYDescripcionConfiguracion(
                CONF_DATOS_TIENDA, nombreNuevo, descripcion
        );
        this.proveedorConfiguracion.invalidarCache(CONF_DATOS_TIENDA);
    }

    public void actualizarMaxIntentos(int nuevoMaximo) {
        this.repositorioConfiguracion.actualizarValorConfiguracion(
                CONF_MAX_INTENTOS, String.valueOf(nuevoMaximo)
        );
        this.proveedorConfiguracion.invalidarCache(CONF_MAX_INTENTOS);
    }

    public void actualizarMaxMinutosBloqueos(int nuevosMinutosBloqueo) {
        this.repositorioConfiguracion.actualizarValorConfiguracion(
                CONF_MINUTOS_BLOQUEO, String.valueOf(nuevosMinutosBloqueo)
        );
        this.proveedorConfiguracion.invalidarCache(CONF_MINUTOS_BLOQUEO);
    }

}//===================================================================================================================//

