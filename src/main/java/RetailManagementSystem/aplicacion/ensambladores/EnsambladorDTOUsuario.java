package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.seguridad.ResultadoRegistroDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOBasico;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOUsuario {

    //CONSTRUCTOR:

    public EnsambladorDTOUsuario() {
    }

    //MÉTODOS:

    public UsuarioDTOCompleto ensamblarDTOUsuarioCompleto(Usuario usuario){
        if (usuario == null){
            throw new IllegalArgumentException("NO puedes ensamblar un Usuario Nulo.");
        }
        return new UsuarioDTOCompleto(
                usuario.getIdUsuario(), usuario.getNombre(), usuario.getApellido(), usuario.getEmail(),
                usuario.isActivo(), usuario.isDebeCambiarContrasena(), usuario.obtenerNombresRoles(),
                usuario.obtenerPermisosTotales());
    }

    public UsuarioDTOBasico ensamblarDTOUsuarioBasico(Usuario usuario){
        if (usuario == null){
            throw new IllegalArgumentException("NO puedes ensamblar un Usuario Nulo.");
        }
        return new UsuarioDTOBasico(
                usuario.getIdUsuario(), usuario.getNombre(), usuario.getApellido(), usuario.getEmail(),
                usuario.isActivo(), usuario.isDebeCambiarContrasena()
        );
    }

    public ResultadoRegistroDTO ensamblarDTOResultadoRegistro(UsuarioDTOBasico usuario, char[] contrasena){
        return new ResultadoRegistroDTO(usuario, contrasena);
    }

    public List<UsuarioDTOBasico> ensamblarDetalleUsuarios(List<Usuario> listaUsuarios){
        List<UsuarioDTOBasico> detalleUsuarios = new ArrayList<>();
        for (Usuario usuario:listaUsuarios){
            UsuarioDTOBasico dto = this.ensamblarDTOUsuarioBasico(usuario);
            detalleUsuarios.add(dto);
        }
        return detalleUsuarios;
    }

}//===================================================================================================================//

