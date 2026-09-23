package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import static RetailManagementSystem.dominio.enums.TipoItem.SERVICIO;

public class Servicio implements ItemFacturable {

    private static final BigDecimal CIEN = new BigDecimal("100");

    //ATRIBUTOS:

    private final String codigoServicio;

    private String nombre;

    private BigDecimal precioBase;

    private Impuesto impuesto;

    private Descuento descuento;

    private Boolean activo;

    //GETTERS Y SETTERS:

    @Override
    public TipoItem getTipoItem() {
        return SERVICIO;
    }

    @Override
    public String getCodigo() {
        return this.codigoServicio;
    }

    @Override
    public String getNombre() {
        return this.nombre;
    }
    private void setNombre(String nombre) {
        this.nombre = nombre.trim();
    }

    public BigDecimal getPrecioBase() {
        return this.precioBase;
    }
    private void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase.setScale(6, RoundingMode.HALF_UP);
    }

    public Impuesto getImpuesto(){
        return this.impuesto;
    }

    public Descuento getDescuento(){
        return this.descuento;
    }

    public boolean isActivo() {
        return activo;
    }

    //VALIDACIONES:

    private void validarCodigo(String codigo){
        if (codigo == null || codigo.isBlank()){
            throw new IllegalArgumentException("El Código del Servicio esta Vacío");
        }
        if (codigo.length() > 50){
            throw new IllegalArgumentException("El Código del Servicio excede los Caracteres Máximos Posibles");
        }
    }

    private void validarNombre(String nombre){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Servicio Vacío");
        }
    }

    private void validarPrecioBase(BigDecimal precioBase){
        if (precioBase == null || precioBase.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Precio Base del Servicio Invalido");
        }
    }

    private static void validarImpuesto(Impuesto impuesto){
        if (impuesto==null){
            throw new IllegalArgumentException("El Servicio Debe Tener Impuesto Obligatoriamente");
        }
    }

    private static void validarEstadoImpuesto(Impuesto impuesto){
        validarImpuesto(impuesto);
        if (!impuesto.isActivo()){
            throw new IllegalArgumentException("El Impuesto que le quieres poner al Servicio esta Inactivo");
        }
    }

    private static void validarDescuento(Descuento descuento){
        if (descuento==null){
            throw new IllegalArgumentException("El Servicio Debe Tener Descuento Obligatoriamente");
        }
    }

    private static void validarEstadoDescuento(Descuento descuento){
        validarDescuento(descuento);
        if (!descuento.isActivo()){
            throw new IllegalArgumentException("El Descuento que le quieres poner al Servicio esta Inactivo");
        }
    }

    //CONSTRUCTOR:

    private Servicio(
            String codigoServicio, String nombre, BigDecimal precioBase, Impuesto impuesto, Descuento descuento,
            Boolean activo
    ) {
        validarCodigo(codigoServicio);
        validarNombre(nombre);
        validarPrecioBase(precioBase);
        validarImpuesto(impuesto);
        validarDescuento(descuento);
        if (activo == null){
            throw new IllegalArgumentException("El Estado del Servicio es Obligatorio");
        }
        this.codigoServicio = codigoServicio;
        setNombre(nombre);
        setPrecioBase(precioBase);
        this.impuesto = impuesto;
        this.descuento = descuento;
        this.activo = activo;
    }

    public static Servicio reconstruirDesdeBD(
            String codigoServicio, String nombre, BigDecimal precioBase, Impuesto impuesto, Descuento descuento,
            Boolean activo
    ) {
        return new Servicio(codigoServicio, nombre, precioBase, impuesto, descuento, activo);
    }

    public static Servicio crearNuevo(
            String nombre, BigDecimal precioBase, Impuesto impuesto, Descuento descuento
    ) {
        validarEstadoImpuesto(impuesto);
        validarEstadoDescuento(descuento);
        return new Servicio(UUID.randomUUID().toString(), nombre, precioBase, impuesto, descuento, true);
    }

    //MÉTODOS:

    private BigDecimal dividirEntreCien(BigDecimal valor){
        return valor.divide(CIEN, 6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularImpuesto(BigDecimal precioFinalSinImpuesto) {
        BigDecimal porcentajeImpuesto = this.impuesto.isActivo() ? this.impuesto.getPorcentaje() : BigDecimal.ZERO;
        return precioFinalSinImpuesto.multiply(
                dividirEntreCien(porcentajeImpuesto)
        )
        .setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal precioBase) {
        BigDecimal porcentajeDescuento = this.descuento.isActivo() ? this.descuento.getPorcentaje() : BigDecimal.ZERO;
        return precioBase.multiply(
                dividirEntreCien(porcentajeDescuento)
        )
        .setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getValorFinalSinImpuesto(LocalDate fecha) {
        return getPrecioBase().subtract(
                calcularDescuento(
                        getPrecioBase()
                )
        )
        .setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getValorVenta(LocalDate fecha) {
        BigDecimal precioFinalSinImpuesto = getValorFinalSinImpuesto(fecha);
        return precioFinalSinImpuesto.add(
                calcularImpuesto(precioFinalSinImpuesto)
        )
        .setScale(6, RoundingMode.HALF_UP);
    }

    //MÉTODOS MODIFICAR SERVICIO:

    public void cambiarNombreServicio(String nombreServicio){
        validarNombre(nombreServicio);
        setNombre(nombreServicio);
    }

    public void cambiarPrecioBase(BigDecimal precioNuevo){
        validarPrecioBase(precioNuevo);
        setPrecioBase(precioNuevo);
    }

    public void cambiarImpuesto(Impuesto impuesto){
        validarImpuesto(impuesto);
        validarEstadoImpuesto(impuesto);
        this.impuesto = impuesto;
    }

    public void cambiarDescuento(Descuento descuento){
        validarDescuento(descuento);
        validarEstadoDescuento(descuento);
        this.descuento = descuento;
    }

    public void cambiarEstado(){
        this.activo = !this.isActivo();
    }

}//===================================================================================================================//

