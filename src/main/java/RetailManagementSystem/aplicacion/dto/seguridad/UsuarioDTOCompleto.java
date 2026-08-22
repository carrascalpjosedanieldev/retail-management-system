package RetailManagementSystem.aplicacion.dto.seguridad;

import java.util.List;

public record UsuarioDTOCompleto(
        Long idUsuario, String nombre, String apellido, String email, boolean activo, boolean debeCambiarContrasena,
        List<RolDTO> roles, List<String> permisos
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

