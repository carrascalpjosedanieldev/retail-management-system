package RetailManagementSystem.dominio.excepciones;

public class ServicioNoDisponibleExeption extends RuntimeException {
    public ServicioNoDisponibleExeption(String message) {
        super(message);
    }
}
