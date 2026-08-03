package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

public interface RepositorioUsuario {

    //CREATE:

    Usuario insertarUsuarioNuevo(Usuario usuario);

    //READ:

    Usuario obtenerUsuarioPorEmail(String email);

    //UPDATE:

    void actualizarUsuario(Usuario usuario);

}

