package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

import java.util.List;
import java.util.Optional;

public interface RepositorioUsuario {

    //CREATE:

    Usuario insertarUsuarioNuevo(Usuario usuario);

    //READ:

    Usuario obtenerUsuarioPorEmail(String email);

    Usuario obtenerUsuarioPorId(Long idUsuario);

    List<Usuario> obtenerTodosLosUsuarios();

    //UPDATE:

    void actualizarDatosLoginUsuario(Usuario usuario);

    void actualizarDatosUsuario(Usuario usuario);

    void actualizarSeguridad(Usuario usuario);

    void actualizarRolesUsuario(Usuario usuario);

}

