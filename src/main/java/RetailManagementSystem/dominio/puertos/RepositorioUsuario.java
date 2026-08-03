package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

public interface RepositorioUsuario {

    //CREATE:

    Usuario insertarUsuarioNuevo(Usuario usuario);

    //READ:

    Usuario obtenerUsuarioPorEmail(String email);

    Usuario obtenerUsuarioPorId(int idUsuario);

    //UPDATE:

    void actualizarDatosLoginUsuario(Usuario usuario);

    void actualizarDatosUsuario(Usuario usuario);

    void actualizarSeguridad(Usuario usuario);

}

