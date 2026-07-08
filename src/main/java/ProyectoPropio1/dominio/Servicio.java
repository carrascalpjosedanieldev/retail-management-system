package ProyectoPropio1.dominio;

import ProyectoPropio1.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import static ProyectoPropio1.dominio.enums.TipoItem.SERVICIO;

public class Servicio implements ItemFacturable {

    //ATRIBUTOS:

    private String nombre;

    private BigDecimal precioBase;

    private final String codigoServicio;

    private final Impuesto impuesto;

    //GETTERS Y SETTERS:

    @Override
    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    private void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    @Override
    public String getCodigo() {
        return this.codigoServicio;
    }

    @Override
    public BigDecimal getValorVenta(LocalDate fecha) {
        BigDecimal impuesto = calcularImpuesto(fecha);
        BigDecimal precioFinal = getPrecioBase().add(impuesto);
        return precioFinal.setScale(4, RoundingMode.HALF_UP);
    }

    public Impuesto getImpuesto(){
        return this.impuesto;
    }

    public int getIdImpuesto(){
        return this.impuesto.getId();
    }

    @Override
    public BigDecimal getPorcentajeImpuesto(){
        return this.impuesto.getPorcentaje();
    }

    //CONSTRUCTOR:

    public Servicio(String codigoServicio, String nombre, BigDecimal precioBase, Impuesto impuesto){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Servicio Vacio");
        }
        if (precioBase.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Precio del Servicio Invalido");
        }
        if (impuesto==null){
            throw new IllegalArgumentException("Impuesto del Servicio Invalido");
        }
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.codigoServicio = codigoServicio;
        this.impuesto = impuesto;
    }

    public Servicio(String nombre, BigDecimal precioBase, Impuesto impuesto){
        this(UUID.randomUUID().toString(), nombre, precioBase, impuesto);
    }

    //METODOS:

    @Override
    public BigDecimal calcularImpuesto(LocalDate fecha) {
        BigDecimal factorImpuesto = getPorcentajeImpuesto().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return getPrecioBase().multiply(factorImpuesto);
    }

    @Override
    public TipoItem getTipoItem() {
        return SERVICIO;
    }

    //METODOS MODIFICAR SERVICIO:

    public void cambiarNombreServicio(String nombreServicio){
        if (nombreServicio==null || nombreServicio.isBlank()){
            throw new IllegalArgumentException("Nombre del Servicio Vacio");
        }
        setNombre(nombreServicio);
    }

    public void cambiarPrecioBase(BigDecimal precioNuevo){
        if (precioNuevo.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Precio del Servicio Invalido");
        }
        setPrecioBase(precioNuevo);
    }

}

