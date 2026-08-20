package RetailManagementSystem.dominio.excepciones.reglasDeNegocio;

public class CapacidadInventarioExcedidaException extends RuntimeException {
    public CapacidadInventarioExcedidaException(String message) {
        super(message);
    }
}

