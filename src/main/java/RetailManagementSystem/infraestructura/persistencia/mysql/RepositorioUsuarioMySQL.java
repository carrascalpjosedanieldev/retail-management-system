package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.puertos.RepositorioUsuario;

public class RepositorioUsuarioMySQL implements RepositorioUsuario {

    @Override
    public Usuario obtenerUsuarioPorEmail(String email) {
        return null;
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {

    }
}

