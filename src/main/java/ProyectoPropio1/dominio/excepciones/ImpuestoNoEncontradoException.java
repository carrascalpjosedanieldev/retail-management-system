package ProyectoPropio1.dominio.excepciones;

public class ImpuestoNoEncontradoException extends RuntimeException {
    public ImpuestoNoEncontradoException(String message) {
        super(message);
    }
}
