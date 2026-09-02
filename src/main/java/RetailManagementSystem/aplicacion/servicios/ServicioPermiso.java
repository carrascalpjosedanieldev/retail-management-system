package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPermiso;

import java.util.ArrayList;
import java.util.List;

public class ServicioPermiso {

    //ATRIBUTOS:

    private final RepositorioPermiso repositorioPermiso;

    //CONSTRUCTOR:

    public ServicioPermiso(RepositorioPermiso repositorioPermiso) {
        this.repositorioPermiso = repositorioPermiso;
    }

    //MÉTODOS:

    public List<Permiso> obtenerPermisosActivos(){
        return new ArrayList<>(this.repositorioPermiso.obtenerPermisosActivos());
    }

    public List<Permiso> obtenerPermisosInactivos(){
        return new ArrayList<>(this.repositorioPermiso.obtenerPermisosInactivos());
    }

    public void cambiarEstadoPermiso(int idPermiso, boolean activoActual){
        this.repositorioPermiso.cambiarEstado(idPermiso, !activoActual);
    }

}//===================================================================================================================//

