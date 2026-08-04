package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTO;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

public class EnsambladorDTOUsuario {

    //MÉTODOS:

    public UsuarioDTO ensamblarDTOUsuario(Usuario usuario){
        if (usuario == null){
            throw new IllegalArgumentException("NO puedes ensamblar un Usuario Nulo.");
        }
        return new UsuarioDTO(
                usuario.getIdUsuario(), usuario.getNombre(), usuario.getApellido(), usuario.getEmail(),
                usuario.isActivo(), usuario.isDebeCambiarContrasena(), usuario.obtenerNombresRoles(),
                usuario.obtenerPermisosTotales());
    }

}//===================================================================================================================//

