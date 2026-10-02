package RetailManagementSystem.infraestructura.seguridad;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;

import java.util.List;

public class ValidadorSeguridad {

    //CONSTRUCTOR:

    private ValidadorSeguridad() { }

    //MÉTODOS:

    private static void validarUsuario(UsuarioDTOCompleto usuario){
        if (usuario == null) {
            throw new AccesoDenegadoException("NO hay una Sesión de Usuario Activa.");
        }
    }

    public static void exigirPermiso(UsuarioDTOCompleto usuario, String permisoRequerido) {
        validarUsuario(usuario);
        if (!usuario.tienePermiso(permisoRequerido.toUpperCase())) {
            throw new AccesoDenegadoException(
                    "Acceso Denegado: Se Requiere el Permiso [" + permisoRequerido + "] Para esta Acción."
            );
        }
    }

    public static void exigirAlgunPermiso(UsuarioDTOCompleto usuario, List<String> permisosValidos) {
        validarUsuario(usuario);
        boolean tieneAcceso = permisosValidos.stream()
                .anyMatch(p -> usuario.tienePermiso(p.toUpperCase()));
        if (!tieneAcceso) {
            throw new AccesoDenegadoException("Acceso Denegado: NO tienes los Privilegios Necesarios.");
        }
    }

}//===================================================================================================================//

