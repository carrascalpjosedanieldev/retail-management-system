package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTO;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOUsuario {

    //CONSTRUCTOR:

    public EnsambladorDTOUsuario() {
    }

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

    public List<UsuarioDTO> ensamblarDetalleUsuarios(List<Usuario> listaUsuarios){
        List<UsuarioDTO> detalleUsuarios = new ArrayList<>();
        for (Usuario usuario:listaUsuarios){
            UsuarioDTO dto = this.ensamblarDTOUsuario(usuario);
            detalleUsuarios.add(dto);
        }
        return detalleUsuarios;
    }

}//===================================================================================================================//

