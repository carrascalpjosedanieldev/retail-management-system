package Excepciones;

public class ServicioNoEncontradoException extends RuntimeException {
    public ServicioNoEncontradoException(String message) {
        super(message);
    }
}
