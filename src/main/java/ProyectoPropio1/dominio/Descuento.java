package ProyectoPropio1.dominio;

import java.math.BigDecimal;

public class Descuento {

    //ATRIBUTOS:

    private final Integer id;

    private String nombre;

    private BigDecimal porcentaje;

    private boolean activo;

    //GETTERS Y SETTERS:

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

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

    //METODOS:

    public void cambiarNombre(String nombreNuevo){
        if (nombreNuevo==null || nombreNuevo.isBlank()){
            throw new IllegalArgumentException("Nombre del Descuento Vacio");
        }
        if (nombreNuevo.equals(getNombre())){
            throw new IllegalArgumentException("El Nombre a colocar al Descuento es el mismo");
        }
        setNombre(nombreNuevo);
    }

    public void cambiarPorcentaje(BigDecimal porcentajeNuevo){
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Porcentaje para el Descuento Invalido");
        }
        setPorcentaje(porcentajeNuevo);
    }

}

