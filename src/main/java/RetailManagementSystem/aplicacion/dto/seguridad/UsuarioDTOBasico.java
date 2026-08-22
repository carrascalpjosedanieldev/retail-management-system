package RetailManagementSystem.aplicacion.dto.seguridad;

public record UsuarioDTOBasico(
        Long idUsuario, String nombre, String apellido, String email, boolean activo, boolean debeCambiarContrasena
) { }

