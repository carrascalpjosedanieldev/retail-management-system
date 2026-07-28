package ProyectoPropio1.dominio.excepciones;

public class ProductoNoDisponibleException extends RuntimeException {
    public ProductoNoDisponibleException(String message) {
        super(message);
    }
}
