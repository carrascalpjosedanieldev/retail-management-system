package RetailManagementSystem.dominio.entidades.gestion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Impuesto {

    private static final BigDecimal CIEN = new BigDecimal("100");

    //ATRIBUTOS:

    private final Integer id;

    private String nombre;

    private BigDecimal porcentaje;

    private Boolean activo;

    //GETTERS:

    public Integer getId() { return id; }

    public String getNombre() { return nombre; }
    private void setNombre(String nombre){
        this.nombre = nombre.trim();
    }

    public BigDecimal getPorcentaje() {
        return this.activo ? this.porcentaje : BigDecimal.ZERO;
    }
    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isActivo() {
        return activo;
    }

    //VALIDACIONES:

    private void validarNombre(String nombre){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("El Nombre del Impuesto NO puede estar Vacío");
        }
    }

    private void validarPorcentaje(BigDecimal porcentaje){
        if (porcentaje == null){
            throw new IllegalArgumentException("El Porcentaje del Impuesto NO puede ser Nulo");
        }
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(CIEN) > 0) {
            throw new IllegalArgumentException("Porcentaje de Impuesto Invalido:  " + porcentaje + "%");
        }
    }

    //CONSTRUCTORES:

    private Impuesto(Integer id, String nombre, BigDecimal porcentaje, Boolean activo) {
        validarNombre(nombre);
        validarPorcentaje(porcentaje);
        if (activo == null){
            throw new IllegalArgumentException("El Estado del Impuesto es Obligatorio");
        }
        this.id = id;
        setNombre(nombre);
        setPorcentaje(porcentaje);
        this.activo = activo;
    }

    public static Impuesto reconstruirDesdeBD(Integer id, String nombre, BigDecimal porcentaje, Boolean activo) {
        return new Impuesto(id, nombre, porcentaje, activo);
    }

    public static Impuesto crearNuevo(String nombre, BigDecimal porcentaje, Boolean activo) {
        return new Impuesto(null, nombre, porcentaje, activo);
    }

    //MÉTODOS:

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Impuesto impuesto = (Impuesto) o;
        if (this.id == null || impuesto.getId() == null) {
            return false;
        }
        return Objects.equals(this.id, impuesto.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    public void cambiarNombre(String nombreNuevo){
        validarNombre(nombreNuevo);
        setNombre(nombreNuevo);
    }

    public void cambiarPorcentaje(BigDecimal porcentajeNuevo){
        validarPorcentaje(porcentajeNuevo);
        setPorcentaje(porcentajeNuevo);
    }

    public void cambiarEstado(){
        this.activo = !this.isActivo();
    }

}//===================================================================================================================//

