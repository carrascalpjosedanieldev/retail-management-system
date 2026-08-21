package RetailManagementSystem.infraestructura.persistencia.excepciones;

public class IdAutogeneradoNoRecibidoException extends RuntimeException {
    public IdAutogeneradoNoRecibidoException(String message) {
        super(message);
    }
    public IdAutogeneradoNoRecibidoException(String message, Throwable causa) {
        super(message, causa);
    }
}
