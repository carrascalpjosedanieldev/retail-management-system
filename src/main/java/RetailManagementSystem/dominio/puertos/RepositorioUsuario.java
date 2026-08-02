package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

import java.time.LocalDateTime;

public interface RepositorioUsuario {

    Usuario obtenerUsuarioPorEmail(String email);

    void actualizarUsuario(Usuario usuario);


}

