package RetailManagementSystem.dominio.excepciones.reglasDeNegocio;

public class CarritoVacioException extends RuntimeException {
    public CarritoVacioException(String message) {
        super(message);
    }
}

