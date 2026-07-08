package ProyectoPropio1.dominio;

import java.math.BigDecimal;

public class Impuesto {

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

    private Impuesto(Integer id, String nombre, BigDecimal porcentaje, boolean activo) {
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Porcentaje de Impuesto Invalido");
        }
        this.id = id;
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.activo = activo;
    }

    public static Impuesto reconstruirDesdeBD(int id, String nombre, BigDecimal porcentaje, boolean activo) {
        return new Impuesto(id, nombre, porcentaje, activo);
    }

    private Impuesto(String nombre, BigDecimal porcentaje) {
        this(null, nombre, porcentaje, true);
    }

    public static Impuesto crearNuevo(String nombre, BigDecimal porcentaje) {
        return new Impuesto(nombre, porcentaje);
    }

}
