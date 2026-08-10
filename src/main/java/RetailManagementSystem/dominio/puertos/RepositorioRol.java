package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.seguridad.Rol;

import java.util.List;

public interface RepositorioRol {

    //CREATE:

    void insertarRol(Rol rolNuevo);

    //READ:

    Rol obtenerRol(int idRol);

    List<Rol> obtenerRoles();

    //UPDATE:

    void actualizarRol(Rol rol);

}//===================================================================================================================//

