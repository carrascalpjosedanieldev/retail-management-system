package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.entidades.Tienda;
import RetailManagementSystem.dominio.puertos.RepositorioConfiguracion;
import RetailManagementSystem.infraestructura.configuracion.ProveedorConfiguracionImpl;

public class ServicioConfiguraciones {

    //ATRIBUTOS:

    private static final String CONF_NOMBRE_TIENDA = "NOMBRE_PROYECTO_PROPIO_ORIGINAL";

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

    public void cambiarNombreYDescripcionTienda(String nombreNuevo, Tienda tienda, String descripcion){
        tienda.cambiarNombreTienda(nombreNuevo);
        this.repositorioConfiguracion.actualizarValorYDescripcionConfiguracion(
                CONF_NOMBRE_TIENDA, nombreNuevo, descripcion
        );
    }

    //MÉTODOS ESPECÍFICOS:

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

