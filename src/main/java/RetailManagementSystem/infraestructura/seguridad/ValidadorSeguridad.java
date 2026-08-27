package RetailManagementSystem.infraestructura.seguridad;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;

import java.util.List;

public class ValidadorSeguridad {

    public static void exigirPermiso(UsuarioDTOCompleto usuario, String permisoRequerido) {
        if (usuario == null) {
            throw new AccesoDenegadoException("NO hay una Sesión de Usuario Activa.");
        }
        if (!usuario.tienePermiso(permisoRequerido.toUpperCase())) {
            throw new AccesoDenegadoException(
                    "Acceso Denegado: Se Requiere el Permiso [" + permisoRequerido + "] Para esta Acción."
            );
        }
    }

    public static void exigirAlgunPermiso(UsuarioDTOCompleto usuario, List<String> permisosValidos) {
        if (usuario == null) {
            throw new AccesoDenegadoException("No hay una sesión de usuario activa.");
        }
        boolean tieneAcceso = permisosValidos.stream()
                .anyMatch(p -> usuario.tienePermiso(p.toUpperCase()));
        if (!tieneAcceso) {
            throw new AccesoDenegadoException("Acceso Denegado: No tienes los privilegios necesarios.");
        }
    }

}//===================================================================================================================//

