package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;

import java.util.List;

public interface RepositorioPermiso {

    //READ:

    Permiso obtenerPermisoPorId(int idPermiso);

    List<Permiso> obtenerPermisosActivos();

    List<Permiso> obtenerTodosLosPermisos();

    List<String> obtenerNombresTodosLosPermisos();

    //UPDATE:

    void actualizarPermiso(Permiso permiso);

}//===================================================================================================================//

