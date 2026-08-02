package RetailManagementSystem.aplicacion.dto;

import RetailManagementSystem.dominio.entidades.seguridad.Rol;

import java.util.Set;

public record UsuarioDTO(
        int idUsuario, String nombre, String apellido, Set<Rol> roles, Set<String> permisos, boolean activo
) { }

