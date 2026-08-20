package RetailManagementSystem.dominio.excepciones.reglasDeNegocio;

public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String message) {
        super(message);
    }
}

