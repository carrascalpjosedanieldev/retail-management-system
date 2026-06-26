package ProyectoPropio1.excepciones;

public class InventarioNoEncontradoException extends RuntimeException {
    public InventarioNoEncontradoException(String message) {
        super(message);
    }
}

