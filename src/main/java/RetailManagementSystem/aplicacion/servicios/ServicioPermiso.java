package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPermiso;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.util.List;

public class ServicioPermiso {

    //ATRIBUTOS:

    private final RepositorioPermiso repositorioPermiso;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioPermiso(RepositorioPermiso repositorioPermiso, GestorTransaccional gestorTransaccional) {
        this.repositorioPermiso = repositorioPermiso;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public List<Permiso> obtenerPermisosActivos(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioPermiso::obtenerPermisosActivos
        );
    }

    public List<Permiso> obtenerTodosLosPermisos(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioPermiso::obtenerTodosLosPermisos
        );
    }

    public List<String> obtenerTodosLosNombresPermisos(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioPermiso::obtenerNombresTodosLosPermisos
        );
    }

    public void cambiarDescripcionPermiso(int idPermiso, String descripcion){
        if (descripcion == null || descripcion.isBlank()){
            throw new IllegalArgumentException("Descripción del Permiso Vacía");
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->
            this.repositorioPermiso.cambiarDescripcion(idPermiso, descripcion)
        );
    }

    public void cambiarEstadoPermiso(int idPermiso, boolean activoActual){
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                this.repositorioPermiso.cambiarEstado(idPermiso, !activoActual)
        );
    }

}//===================================================================================================================//

