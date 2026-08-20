package RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad;

public class PermisoNoDisponibleException extends RuntimeException {
    public PermisoNoDisponibleException(String message) {
        super(message);
    }
}
