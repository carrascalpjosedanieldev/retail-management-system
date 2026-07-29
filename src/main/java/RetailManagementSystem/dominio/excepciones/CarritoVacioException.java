package RetailManagementSystem.dominio.excepciones;

public class CarritoVacioException extends RuntimeException {
    public CarritoVacioException(String message) {
        super(message);
    }
}

