package RetailManagementSystem.vista.excepciones;

public class CargarVistaException extends RuntimeException {
    private final String rutaSolicitada;

    public CargarVistaException(String rutaSolicitada, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.rutaSolicitada = rutaSolicitada;
    }

    public String getRutaSolicitada() {
        return rutaSolicitada;
    }
}
