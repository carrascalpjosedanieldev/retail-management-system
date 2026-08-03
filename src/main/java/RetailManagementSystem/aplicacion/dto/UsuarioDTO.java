package RetailManagementSystem.aplicacion.dto;

import java.util.Set;

public record UsuarioDTO(
        int idUsuario, String nombre, String apellido, String email, boolean activo, boolean debeCambiarContrasena,
        Set<String> roles, Set<String> permisos
) {
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean tienePermiso(String permisoRequerido) {
        return permisos.contains(permisoRequerido);
    }

    public boolean tieneRol(String rolRequerido) {
        return roles.contains(rolRequerido);
    }
}

