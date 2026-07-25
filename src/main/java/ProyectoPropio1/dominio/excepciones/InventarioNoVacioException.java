package ProyectoPropio1.dominio.excepciones;

public class InventarioNoVacioException extends RuntimeException {
    public InventarioNoVacioException(String message) {
        super(message);
    }
}

