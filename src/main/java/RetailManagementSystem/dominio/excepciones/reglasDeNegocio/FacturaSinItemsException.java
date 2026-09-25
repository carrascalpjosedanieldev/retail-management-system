package RetailManagementSystem.dominio.excepciones.reglasDeNegocio;

public class FacturaSinItemsException extends RuntimeException {
    public FacturaSinItemsException(String message) {
        super(message);
    }
}
