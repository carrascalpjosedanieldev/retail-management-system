package RetailManagementSystem.dominio.excepciones.reglasDeNegocio;

public class ProductoNoDisponibleException extends RuntimeException {
    public ProductoNoDisponibleException(String message) {
        super(message);
    }
}

