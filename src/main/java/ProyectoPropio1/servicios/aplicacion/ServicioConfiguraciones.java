package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Tienda;
import ProyectoPropio1.dominio.puertos.RepositorioConfiguracion;

public class ServicioConfiguraciones {

    private final RepositorioConfiguracion repositorioConfiguracion;

    public ServicioConfiguraciones(RepositorioConfiguracion repositorioConfiguracion) {
        this.repositorioConfiguracion = repositorioConfiguracion;
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

    public void cambiarNombreYDescripcionTienda(String claveTienda, String nombreNuevo, Tienda tienda, String descripcion){
        tienda.cambiarNombreTienda(nombreNuevo);
        this.repositorioConfiguracion.actualizarValorYDescripcionConfiguracion(claveTienda, nombreNuevo, descripcion);
    }

}
