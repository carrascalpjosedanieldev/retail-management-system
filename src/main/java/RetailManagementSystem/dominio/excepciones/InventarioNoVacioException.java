package RetailManagementSystem.dominio.excepciones;

public class InventarioNoVacioException extends RuntimeException {
    public InventarioNoVacioException(String message) {
        super(message);
    }
}

