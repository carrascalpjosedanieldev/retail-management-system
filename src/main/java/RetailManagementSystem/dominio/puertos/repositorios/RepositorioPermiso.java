package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;

import java.util.List;

public interface RepositorioPermiso {

    //READ:

    List<Permiso> obtenerPermisosActivos();

    List<Permiso> obtenerTodosLosPermisos();

    List<String> obtenerNombresTodosLosPermisos();

    //UPDATE:

    void cambiarEstado(int idPermiso, boolean activo);

    void cambiarDescripcion(int idPermiso, String descripcion);

}//===================================================================================================================//

