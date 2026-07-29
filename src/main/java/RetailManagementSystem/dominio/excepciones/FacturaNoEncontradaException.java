package RetailManagementSystem.dominio.excepciones;

public class FacturaNoEncontradaException extends RuntimeException {
    public FacturaNoEncontradaException(String message) {
        super(message);
    }
}

