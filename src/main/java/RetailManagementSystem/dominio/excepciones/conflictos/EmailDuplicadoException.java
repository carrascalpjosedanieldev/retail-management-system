package RetailManagementSystem.dominio.excepciones.conflictos;

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String message) {
        super(message);
    }
}
