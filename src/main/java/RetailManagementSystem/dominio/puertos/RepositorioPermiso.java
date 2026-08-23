package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;

import java.util.List;

public interface RepositorioPermiso {

    //READ:

    Permiso obtenerPermiso(int idPermiso);

    List<Permiso> obtenerPermisosActivos();

    List<Permiso> obtenerPermisosInactivos();

    List<String> obtenerNombresTodosLosPermisos();

    //UPDATE:

    void cambiarEstado(int idPermiso, boolean activo);

}//===================================================================================================================//

