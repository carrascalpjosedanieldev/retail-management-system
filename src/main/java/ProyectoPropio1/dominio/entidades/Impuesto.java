package ProyectoPropio1.dominio.entidades;

import java.math.BigDecimal;

public class Impuesto {

    //ATRIBUTOS:

    private final Integer id;

    private String nombre;

    private BigDecimal porcentaje;

    private boolean activo;

    //GETTERS Y SETTERS:

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPorcentaje() { return porcentaje; }
    private void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public boolean isActivo() {
        return activo;
    }
    private void setActivo(boolean activo) {
        this.activo = activo;
    }

    //CONSTRUCTORES:

    private Impuesto(Integer id, String nombre, BigDecimal porcentaje, boolean activo) {
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Impuesto Vacío");
        }
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

    private Impuesto(String nombre, BigDecimal porcentaje, boolean activo) {
        this(null, nombre, porcentaje, activo);
    }

    public static Impuesto crearNuevo(String nombre, BigDecimal porcentaje, boolean activo) {
        return new Impuesto(nombre, porcentaje, activo);
    }

    //MÉTODOS:

    public void cambiarNombre(String nombreNuevo){
        if (nombreNuevo==null || nombreNuevo.isBlank()){
            throw new IllegalArgumentException("Nombre del Impuesto Vacío");
        }
        setNombre(nombreNuevo);
    }

    public void cambiarPorcentaje(BigDecimal porcentajeNuevo){
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Porcentaje para el Impuesto Invalido");
        }
        setPorcentaje(porcentajeNuevo);
    }

    public void activar(){
        if (isActivo()){
            throw new IllegalStateException("El Impuesto ya esta Activo");
        }
        setActivo(true);
    }

    public void desactivar(){
        if (!isActivo()){
            throw new IllegalStateException("El Impuesto ya esta Inactivo");
        }
        setActivo(false);
    }

}
