package RetailManagementSystem.aplicacion.dto.seguridad;

public record PermisoDTO(
        int idPermiso, String nombre, String descripcion, String modulo, boolean activo
) { }

