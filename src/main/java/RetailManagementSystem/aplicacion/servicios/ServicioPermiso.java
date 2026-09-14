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

    public Permiso actualizarPermiso(int idPermiso, String descripcion){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()-> {
            Permiso permiso = this.repositorioPermiso.obtenerPermisoPorId(idPermiso);
            permiso.cambiarDescripcion(descripcion);
            this.repositorioPermiso.actualizarPermiso(permiso);
            return permiso;
        });
    }

    public void cambiarEstadoPermiso(int idPermiso){
        this.gestorTransaccional.ejecutarEnTransaccion(()-> {
            Permiso permiso = this.repositorioPermiso.obtenerPermisoPorId(idPermiso);
            permiso.cambiarEstado();
            this.repositorioPermiso.actualizarPermiso(permiso);
        });
    }

}//===================================================================================================================//

