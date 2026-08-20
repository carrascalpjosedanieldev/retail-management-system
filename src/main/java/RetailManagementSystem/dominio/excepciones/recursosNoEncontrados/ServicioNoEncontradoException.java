package RetailManagementSystem.dominio.excepciones.recursosNoEncontrados;

public class ServicioNoEncontradoException extends RuntimeException {
    public ServicioNoEncontradoException(String message) {
        super(message);
    }
}

