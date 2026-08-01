package RetailManagementSystem.dominio.entidades;

import RetailManagementSystem.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import static RetailManagementSystem.dominio.enums.TipoItem.SERVICIO;

public class Servicio implements ItemFacturable {

    //ATRIBUTOS:

    private String nombre;

    private BigDecimal precioBase;

    private final String codigoServicio;

    private Impuesto impuesto;

    private Descuento descuento;

    private boolean activo;

    private static final BigDecimal CIEN = new BigDecimal("100");

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
        BigDecimal descuentoAplicado = calcularDescuento(getPrecioBase(), fecha);
        BigDecimal precioFinalSinImpuesto = precioBase.subtract(descuentoAplicado);
        BigDecimal impuesto = calcularImpuesto(precioFinalSinImpuesto, fecha);
        BigDecimal valorVenta = precioFinalSinImpuesto.add(impuesto);
        return valorVenta.setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getValorFinalSinImpuesto(LocalDate fecha) {
        BigDecimal descuentoAplicado = calcularDescuento(getPrecioBase(), fecha);
        return precioBase.subtract(descuentoAplicado);
    }

    public Impuesto getImpuesto(){
        return this.impuesto;
    }
    public void setImpuesto(Impuesto impuesto) {
        this.impuesto = impuesto;
    }

    public int getIdImpuesto(){
        return this.impuesto.getId();
    }

    @Override
    public BigDecimal getPorcentajeImpuesto(){
        return this.impuesto.getPorcentaje();
    }

    public Descuento getDescuento(){
        return descuento;
    }
    public void setDescuento(Descuento descuento) {
        this.descuento = descuento;
    }

    public int getIdDescuento(){
        return this.descuento.getId();
    }

    @Override
    public BigDecimal getPorcentajeDescuento(){
        return this.descuento.getPorcentaje();
    }

    public boolean isActivo() {
        return activo;
    }

    private void setActivo(boolean activo) {
        this.activo = activo;
    }

    //CONSTRUCTOR:

    private Servicio(String codigoServicio, String nombre, BigDecimal precioBase, Impuesto impuesto, Descuento descuento,
                     boolean activo){
        if (codigoServicio.length() > 50){
            throw new IllegalArgumentException("El Codigo del Servicio execede los Caracteres Maximos Posibles");
        }
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Servicio Vacio");
        }
        if (precioBase.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Precio del Servicio Invalido");
        }
        if (impuesto==null){
            throw new IllegalArgumentException("Impuesto del Servicio Invalido");
        }
        if (descuento==null){
            throw new IllegalArgumentException("Descuento del Servicio Invalido");
        }
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.codigoServicio = codigoServicio;
        this.impuesto = impuesto;
        this.descuento = descuento;
        this.activo = activo;
    }

    public static Servicio reconstruirDesdeBD(String codigoServicio, String nombre, BigDecimal precioBase,
                                              Impuesto impuesto, Descuento descuento, boolean activo){
        return new Servicio(codigoServicio, nombre, precioBase, impuesto, descuento, activo);
    }

    private Servicio(String nombre, BigDecimal precioBase, Impuesto impuesto, Descuento descuento, boolean activo){
        this(UUID.randomUUID().toString(), nombre, precioBase, impuesto, descuento, activo);
    }

    public static Servicio crearNuevo(String nombre, BigDecimal precioBase, Impuesto impuesto, Descuento descuento){
        return new Servicio(nombre, precioBase, impuesto, descuento, true);
    }

    //METODOS:

    @Override
    public TipoItem getTipoItem() {
        return SERVICIO;
    }

    @Override
    public BigDecimal calcularImpuesto(BigDecimal precioFinalSinImpuesto, LocalDate fecha) {
        BigDecimal factorImpuesto = getPorcentajeImpuesto().divide(CIEN, 6, RoundingMode.HALF_UP);
        return precioFinalSinImpuesto.multiply(factorImpuesto);
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal precioBase, LocalDate fecha) {
        if (getPorcentajeDescuento().compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        BigDecimal factorDescuento = getPorcentajeDescuento().divide(CIEN, 6, RoundingMode.HALF_UP);
        return precioBase.multiply(factorDescuento);
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

    public void cambiarImpuesto(Impuesto impuesto){
        if (impuesto == getImpuesto()){
            throw new IllegalArgumentException("El Impuesto nuevo y el vigente son el mismo");
        }
        if (!impuesto.isActivo()){
            throw new IllegalArgumentException("El Impuesto que le quieres poner al Servicio esta Inactivo");
        }
        setImpuesto(impuesto);
    }

    public void cambiarDescuento(Descuento descuento){
        if (descuento == getDescuento()){
            throw new IllegalArgumentException("El Descuento nuevo y el vigente son el mismo");
        }
        if (!descuento.isActivo()){
            throw new IllegalArgumentException("El Descuento que le quieres poner al Servicio esta Inactivo");
        }
        setDescuento(descuento);
    }

    public void activarServicio(){
        if (isActivo()){
            throw new IllegalStateException("El Servicio ya esta Activo");
        }
        setActivo(true);
    }

    public void desactivarServicio(){
        if (!isActivo()){
            throw new IllegalStateException("El Servicio ya esta Inactivo");
        }
        setActivo(false);
    }

}//===================================================================================================================//

