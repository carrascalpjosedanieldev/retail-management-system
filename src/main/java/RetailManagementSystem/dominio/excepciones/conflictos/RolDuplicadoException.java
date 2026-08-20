package RetailManagementSystem.dominio.excepciones.conflictos;

public class RolDuplicadoException extends RuntimeException {
    public RolDuplicadoException(String message) {
        super(message);
    }
}
