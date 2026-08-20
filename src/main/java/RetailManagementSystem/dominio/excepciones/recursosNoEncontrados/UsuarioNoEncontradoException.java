package RetailManagementSystem.dominio.excepciones.recursosNoEncontrados;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}
