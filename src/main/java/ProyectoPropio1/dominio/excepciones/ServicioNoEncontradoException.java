package ProyectoPropio1.dominio.excepciones;

public class ServicioNoEncontradoException extends RuntimeException {
    public ServicioNoEncontradoException(String message) {
        super(message);
    }
}

