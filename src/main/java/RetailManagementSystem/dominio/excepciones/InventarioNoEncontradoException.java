package RetailManagementSystem.dominio.excepciones;

public class InventarioNoEncontradoException extends RuntimeException {
    public InventarioNoEncontradoException(String message) {
        super(message);
    }
}

