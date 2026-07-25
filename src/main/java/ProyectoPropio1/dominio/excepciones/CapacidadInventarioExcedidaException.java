package ProyectoPropio1.dominio.excepciones;

public class CapacidadInventarioExcedidaException extends RuntimeException {
    public CapacidadInventarioExcedidaException(String message) {
        super(message);
    }
}
