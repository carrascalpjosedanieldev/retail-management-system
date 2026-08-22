package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.seguridad.ResultadoRegistroDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOUsuario {

    //CONSTRUCTOR:

    public EnsambladorDTOUsuario() {
    }

    //MÉTODOS:

    public UsuarioDTOCompleto ensamblarDTOUsuario(Usuario usuario){
        if (usuario == null){
            throw new IllegalArgumentException("NO puedes ensamblar un Usuario Nulo.");
        }
        return new UsuarioDTOCompleto(
                usuario.getIdUsuario(), usuario.getNombre(), usuario.getApellido(), usuario.getEmail(),
                usuario.isActivo(), usuario.isDebeCambiarContrasena(), usuario.obtenerNombresRoles(),
                usuario.obtenerPermisosTotales());
    }

    public ResultadoRegistroDTO ensamblarDTOResultadoregistro(UsuarioDTOCompleto usuario, char[] contrasena){
        return new ResultadoRegistroDTO(usuario, contrasena);
    }

    public List<UsuarioDTOCompleto> ensamblarDetalleUsuarios(List<Usuario> listaUsuarios){
        List<UsuarioDTOCompleto> detalleUsuarios = new ArrayList<>();
        for (Usuario usuario:listaUsuarios){
            UsuarioDTOCompleto dto = this.ensamblarDTOUsuario(usuario);
            detalleUsuarios.add(dto);
        }
        return detalleUsuarios;
    }

}//===================================================================================================================//

