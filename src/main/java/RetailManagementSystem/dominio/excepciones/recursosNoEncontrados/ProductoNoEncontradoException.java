package RetailManagementSystem.dominio.excepciones.recursosNoEncontrados;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(String message) {
        super(message);
    }
}

