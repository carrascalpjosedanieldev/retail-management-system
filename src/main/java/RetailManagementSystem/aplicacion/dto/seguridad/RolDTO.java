package RetailManagementSystem.aplicacion.dto.seguridad;

import java.util.List;

public record RolDTO(
        int idRol, String nombre, List<PermisoDTO> permisos, boolean activo
) { }

