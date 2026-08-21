package RetailManagementSystem.infraestructura.persistencia.excepciones;

public class IncersionFallidaException extends RuntimeException {
    public IncersionFallidaException(String message) {
        super(message);
    }
    public IncersionFallidaException(String message, Throwable causa) {
        super(message, causa);
    }

}
