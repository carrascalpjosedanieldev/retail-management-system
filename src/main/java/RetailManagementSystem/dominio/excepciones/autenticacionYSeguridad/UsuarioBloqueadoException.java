package RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad;

public class UsuarioBloqueadoException extends RuntimeException {
    public UsuarioBloqueadoException(String message) {
        super(message);
    }
}
