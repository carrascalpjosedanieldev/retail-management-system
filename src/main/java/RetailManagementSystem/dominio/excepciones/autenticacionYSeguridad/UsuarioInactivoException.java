package RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad;

public class UsuarioInactivoException extends RuntimeException {
    public UsuarioInactivoException(String message) {
        super(message);
    }
}
