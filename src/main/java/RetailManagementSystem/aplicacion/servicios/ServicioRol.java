package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.puertos.RepositorioRol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ServicioRol {

    //ATRIBUTOS:

    private final RepositorioRol repositorioRol;

    //CONSTRUCTOR:

    public ServicioRol(RepositorioRol repositorioRol) {
        this.repositorioRol = repositorioRol;
    }

    //MÉTODOS:

    public void registrarRol(String nombre, boolean activo, Set<Permiso> permisos){
        Rol rolNuevo = Rol.crearNuevo(nombre, activo);
        for (Permiso permiso:permisos){
            rolNuevo.anadirPermisoNuevo(permiso);
        }
        this.repositorioRol.insertarRol(rolNuevo);
    }

    public List<Rol> obtenerRoles(){
        List<Rol> roles = new ArrayList<>();
        roles.addAll(this.repositorioRol.obtenerRolesActivos());
        roles.addAll(this.repositorioRol.obtenerRolesInactivos());
        return roles;
    }



}//===================================================================================================================//

