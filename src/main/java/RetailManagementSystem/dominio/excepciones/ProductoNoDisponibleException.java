package RetailManagementSystem.dominio.excepciones;

public class ProductoNoDisponibleException extends RuntimeException {
    public ProductoNoDisponibleException(String message) {
        super(message);
    }
}
