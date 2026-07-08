package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Tienda;
import ProyectoPropio1.dominio.puertos.RepositorioConfiguracion;

public class ServicioConfiguraciones {

    private final RepositorioConfiguracion repositorioConfiguracion;

    public ServicioConfiguraciones(RepositorioConfiguracion repositorioConfiguracion) {
        this.repositorioConfiguracion = repositorioConfiguracion;
    }

    public void cambiarNombreTienda(String nombreNuevo, Tienda tienda){
        String descripcion = this.repositorioConfiguracion.obtenerDescripcionConfiguracion("NombreProyectoPropioOriginal");
        tienda.cambiarNombreTienda(nombreNuevo);
        this.repositorioConfiguracion.actualizarValorConfiguracion("NombreProyectoPropioOriginal", nombreNuevo, descripcion);
    }

}
