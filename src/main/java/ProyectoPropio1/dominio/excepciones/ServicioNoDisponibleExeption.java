package ProyectoPropio1.dominio.excepciones;

public class ServicioNoDisponibleExeption extends RuntimeException {
    public ServicioNoDisponibleExeption(String message) {
        super(message);
    }
}
