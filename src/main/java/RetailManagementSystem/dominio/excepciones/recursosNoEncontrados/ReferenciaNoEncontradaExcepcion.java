package RetailManagementSystem.dominio.excepciones.recursosNoEncontrados;

public class ReferenciaNoEncontradaExcepcion extends RuntimeException {
    public ReferenciaNoEncontradaExcepcion(String message) {
        super(message);
    }
}
