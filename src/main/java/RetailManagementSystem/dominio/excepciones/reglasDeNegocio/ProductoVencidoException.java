package RetailManagementSystem.dominio.excepciones.reglasDeNegocio;

public class ProductoVencidoException extends RuntimeException {
    public ProductoVencidoException(String message) {
        super(message);
    }
}

