package ProyectoPropio1.dominio;

import java.math.BigDecimal;

public class Descuento {

    //ATRIBUTOS:

    private final Integer id;

    private final String nombre;

    private final BigDecimal porcentaje;

    private boolean activo;

    //GETTERS Y SETTERS:

    public int getId() { return id; }

    public String getNombre() { return nombre; }

    public BigDecimal getPorcentaje() { return porcentaje; }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    //CONSTRUCTORES:

    private Descuento(Integer id, String nombre, BigDecimal porcentaje, boolean activo) {
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Porcentaje de Descuento Invalido");
        }
        this.id = id;
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.activo = activo;
    }

    public static Descuento reconstruirDesdeBD(int id, String nombre, BigDecimal porcentaje, boolean activo) {
        return new Descuento(id, nombre, porcentaje, activo);
    }

    private Descuento(String nombre, BigDecimal porcentaje) {
        this(null, nombre, porcentaje, true);
    }

    public static Descuento crearNuevo(String nombre, BigDecimal porcentaje) {
        return new Descuento(nombre, porcentaje);
    }

}

