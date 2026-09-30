package RetailManagementSystem.dominio.entidades.gestion;

import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;

import java.math.BigDecimal;
import java.util.Objects;

public class Descuento {

    //ATRIBUTOS:

    private final Integer id;

    private String nombre;

    private BigDecimal porcentaje;

    private boolean activo;

    //GETTERS

    public Integer getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        this.nombre = nombre.trim();
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }
    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje.setScale(
                MatematicaFinanciera.ESCALA_CALCULO, MatematicaFinanciera.REDONDEO_ESTANDAR
        );
    }

    public boolean isActivo() {
        return activo;
    }

    //VALIDACIONES:

    private void validarNombre(String nombre){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("El Nombre del Descuento NO puede estar Vacío");
        }
    }

    private void validarPorcentaje(BigDecimal porcentaje){
        if (porcentaje == null){
            throw new IllegalArgumentException("El Porcentaje del Descuento NO puede ser Nulo");
        }
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(MatematicaFinanciera.CIEN) > 0) {
            throw new IllegalArgumentException("Porcentaje de Descuento Invalido:  " + porcentaje + "%");
        }
    }

    //CONSTRUCTORES:

    private Descuento(Integer id, String nombre, BigDecimal porcentaje, Boolean activo) {
        validarNombre(nombre);
        validarPorcentaje(porcentaje);
        if (activo == null){
            throw new IllegalArgumentException("El Estado del Descuento es Obligatorio");
        }
        this.id = id;
        setNombre(nombre);
        setPorcentaje(porcentaje);
        this.activo = activo;
    }

    public static Descuento reconstruirDesdeBD(Integer id, String nombre, BigDecimal porcentaje, Boolean activo) {
        return new Descuento(id, nombre, porcentaje, activo);
    }

    public static Descuento crearNuevo(String nombre, BigDecimal porcentaje, Boolean activo) {
        return new Descuento(null, nombre, porcentaje, activo);
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
        Descuento descuento = (Descuento) o;
        if (this.id == null || descuento.getId() == null) {
            return false;
        }
        return Objects.equals(this.id, descuento.getId());
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

