package RetailManagementSystem.dominio.excepciones.reglasDeNegocio;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String message) {
        super(message);
    }
}

