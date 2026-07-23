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
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Descuento Vacio");
        }
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Porcentaje de Descuento Invalido:  " + porcentaje + "%");
        }
        this.id = id;
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.activo = activo;
    }

    public static Descuento reconstruirDesdeBD(int id, String nombre, BigDecimal porcentaje, boolean activo) {
        return new Descuento(id, nombre, porcentaje, activo);
    }

    private Descuento(String nombre, BigDecimal porcentaje, boolean activo) {
        this(null, nombre, porcentaje, activo);
    }

    public static Descuento crearNuevo(String nombre, BigDecimal porcentaje, boolean activo) {
        return new Descuento(nombre, porcentaje, activo);
    }

    //METODOS:

    public void cambiarNombre(String nombreNuevo){
        if (nombreNuevo==null || nombreNuevo.isBlank()){
            throw new IllegalArgumentException("Nombre del Descuento Vacio");
        }
        setNombre(nombreNuevo);
    }

    public void cambiarPorcentaje(BigDecimal porcentajeNuevo){
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Porcentaje para el Descuento Invalido");
        }
        setPorcentaje(porcentajeNuevo);
    }

    public void activar(){
        if (isActivo()){
            throw new IllegalStateException("El Descuento ya esta Activo");
        }
        setActivo(true);
    }

    public void desactivar(){
        if (!isActivo()){
            throw new IllegalStateException("El Descuento ya esta Inactivo");
        }
        setActivo(false);
    }

}

