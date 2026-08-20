package RetailManagementSystem.dominio.excepciones.recursosNoEncontrados;

public class FacturaNoEncontradaException extends RuntimeException {
    public FacturaNoEncontradaException(String message) {
        super(message);
    }
}

