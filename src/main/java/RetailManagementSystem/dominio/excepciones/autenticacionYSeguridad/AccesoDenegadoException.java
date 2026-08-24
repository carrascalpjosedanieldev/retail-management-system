package RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad;

public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String message) {
        super(message);
    }
}
