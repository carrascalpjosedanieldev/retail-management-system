package RetailManagementSystem.infraestructura.seguridad;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;

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

}//===================================================================================================================//

