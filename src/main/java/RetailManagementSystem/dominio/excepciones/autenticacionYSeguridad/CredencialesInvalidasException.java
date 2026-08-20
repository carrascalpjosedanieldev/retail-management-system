package RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
