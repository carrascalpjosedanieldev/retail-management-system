package Excepciones;

public class InventarioNoEncontradoException extends RuntimeException {
    public InventarioNoEncontradoException(String message) {
        super(message);
    }
}

