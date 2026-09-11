package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioRol;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.util.List;

public class ServicioRol {

    //ATRIBUTOS:

    private final RepositorioRol repositorioRol;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioRol(RepositorioRol repositorioRol, GestorTransaccional gestorTransaccional) {
        this.repositorioRol = repositorioRol;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public void registrarRol(String nombre, boolean activo, List<Permiso> permisos){
        Rol rolNuevo = Rol.crearNuevo(nombre, activo);
        for (Permiso permiso:permisos){
            rolNuevo.anadirPermisoNuevo(permiso);
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                this.repositorioRol.insertarRol(rolNuevo)
        );
    }

    public Rol actualzarDatosRol(int idRol, String nombreNuevo, boolean activo){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Rol rol = this.repositorioRol.obtenerRol(idRol);
            rol.cambiarNombre(nombreNuevo);
            if (rol.isActivo() && !activo){
                rol.desactivarRol();
            } else if (!rol.isActivo() && activo){
                rol.activarRol();
            }
            this.repositorioRol.actualizarDatosRol(rol);
            return rol;
        });

    }

    public void actualizarPermisosRol(int idRol, List<Permiso> listaPermisosActualizada){
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            Rol rol = this.repositorioRol.obtenerRol(idRol);
            for (Permiso p:rol.getPermisos().stream().toList()){
                rol.quitarPermiso(p);
            }
            for (Permiso permiso:listaPermisosActualizada){
                rol.anadirPermisoNuevo(permiso);
            }
            this.repositorioRol.actualizarPermisosRol(rol);
        });
    }

    public List<Rol> obtenerRoles(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioRol::obtenerTodosLosRoles
        );
    }



}//===================================================================================================================//

