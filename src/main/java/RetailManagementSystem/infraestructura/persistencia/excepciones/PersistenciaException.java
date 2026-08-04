package RetailManagementSystem.infraestructura.persistencia.excepciones;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String message) {
        super(message);
    }
    public PersistenciaException(String message, Throwable causa){
        super(message, causa);
    }
}
