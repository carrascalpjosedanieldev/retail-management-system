package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.seguridad.Rol;

import java.util.List;

public interface RepositorioRol {

    //CREATE:

    Rol insertarRol(Rol rolNuevo);

    //READ:

    Rol obtenerRol(int idRol);

    List<Rol> obtenerRolesActivos();

    List<Rol> obtenerRolesInactivos();

    //UPDATE:

    void actualizarDatosRol(Rol rol);

    void actualizarPermisosRol(Rol rol);

}//===================================================================================================================//

