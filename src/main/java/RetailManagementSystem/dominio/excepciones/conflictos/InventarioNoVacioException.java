package RetailManagementSystem.dominio.excepciones.conflictos;

public class InventarioNoVacioException extends RuntimeException {
    public InventarioNoVacioException(String message) {
        super(message);
    }
}

