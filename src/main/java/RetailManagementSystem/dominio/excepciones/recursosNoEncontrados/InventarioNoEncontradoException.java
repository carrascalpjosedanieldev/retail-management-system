package RetailManagementSystem.dominio.excepciones.recursosNoEncontrados;

public class InventarioNoEncontradoException extends RuntimeException {
    public InventarioNoEncontradoException(String message) {
        super(message);
    }
}

