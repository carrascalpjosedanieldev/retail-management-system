package RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad;

public class RolNoDisponibleException extends RuntimeException {
    public RolNoDisponibleException(String message) {
        super(message);
    }
}
