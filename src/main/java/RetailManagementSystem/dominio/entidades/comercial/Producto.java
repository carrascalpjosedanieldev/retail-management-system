package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.TipoItem;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.StockInsuficienteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

import static RetailManagementSystem.dominio.enums.TipoItem.PRODUCTO;

public abstract sealed class Producto implements ItemFacturable permits ProductoRopa, ProductoPerecedero{

    protected static final BigDecimal CIEN = new BigDecimal("100");

    //ATRIBUTOS:

    private final String codigo;

    private String nombre;

    private BigDecimal valorCompra;

    private BigDecimal porcentajeGanancia;

    private int stock;

    private Impuesto impuesto;

    private Descuento descuento;

    private boolean activo;

    //GETTERS Y SETTERS:

    @Override
    public TipoItem getTipoItem() {
        return PRODUCTO;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }
    protected void setNombre(String nombre) {
        this.nombre = nombre.trim();
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }
    protected void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra.setScale(6, RoundingMode.HALF_UP);
    }

    public BigDecimal getPorcentajeGanancia() {
        return porcentajeGanancia;
    }
    protected void setPorcentajeGanancia(BigDecimal porcentajeGanancia) {
        this.porcentajeGanancia = porcentajeGanancia.setScale(2, RoundingMode.HALF_UP);
    }

    public int getStock() {
        return stock;
    }

    @Override
    public BigDecimal getValorVenta(LocalDate fecha){
        return calcularValorVenta(fecha);
    }

    public Impuesto getImpuesto() {
        return impuesto;
    }

    public int getIdImpuesto() {
        return impuesto.getId();
    }

    @Override
    public BigDecimal getPorcentajeImpuesto(){
        if (!impuesto.isActivo()){
            return BigDecimal.ZERO;
        }
        return impuesto.getPorcentaje();
    }

    public Descuento getDescuento(){
        return descuento;
    }

    public int getIdDescuento(){
        return descuento.getId();
    }

    @Override
    public BigDecimal getPorcentajeDescuento(){
        if (!descuento.isActivo()) {
            return BigDecimal.ZERO;
        }
        return descuento.getPorcentaje();
    }

    public boolean isActivo(){
        return activo;
    }

    //VALIDACIONES:

    private void validarCodigo(String codigo){
        if (codigo == null || codigo.isBlank()){
            throw new IllegalArgumentException("El Código del Producto esta Vacío");
        }
        if (codigo.length() > 50){
            throw new IllegalArgumentException("El Código del Producto excede los Caracteres Máximos Posibles");
        }
    }

    private void validarNombre(String nombre){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Invalido");
        }
    }

    private void validarValorCompra(BigDecimal valorCompra){
        if (valorCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
    }

    private void validarPorcentajeGanancia(BigDecimal porcentajeGanancia){
        if (porcentajeGanancia.compareTo(BigDecimal.ZERO) <= 0  || porcentajeGanancia.compareTo(CIEN) > 0){
            throw new IllegalArgumentException("Porcentaje de Ganancia Invalido");
        }
    }

    private void validarStock(Integer stock){
        if (stock == null || stock<0){
            throw new IllegalArgumentException("Stock del Producto Invalido");
        }
    }

    private void validarImpuesto(Impuesto impuesto){
        if (impuesto==null){
            throw new IllegalArgumentException("El Producto Debe Tener Impuesto");
        }
    }

    private void validarEstadoImpuesto(Impuesto impuesto){
        if (!impuesto.isActivo()){
            throw new IllegalStateException("El Impuesto que le quieres poner al Producto esta Inactivo");
        }
    }

    private void validarDescuento(Descuento descuento){
        if (descuento==null){
            throw new IllegalArgumentException("El Producto Debe Tener Descuento");
        }
    }

    private void validarEstadoDescuento(Descuento descuento){
        if (!descuento.isActivo()){
            throw new IllegalStateException("El Descuento que le quieres poner al Producto esta Inactivo");
        }
    }

    //CONSTRUCTOR:

        //Reconstruir desde DB
    protected Producto(String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                       Integer stock, Impuesto impuesto, Descuento descuento, Boolean activo){
        validarCodigo(codigo);
        validarNombre(nombre);
        validarValorCompra(valorCompra);
        validarPorcentajeGanancia(porcentajeGanancia);
        validarStock(stock);
        validarImpuesto(impuesto);
        validarDescuento(descuento);
        if (activo == null){
            throw new IllegalArgumentException("El Estado del Producto es Obligatorio");
        }
        this.codigo = codigo;
        setNombre(nombre);
        setValorCompra(valorCompra);
        setPorcentajeGanancia(porcentajeGanancia);
        this.descuento = descuento;
        this.stock = stock;
        this.impuesto = impuesto;
        this.activo = activo;
    }

        //Crear Nuevo
    protected Producto(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, Integer stock,
            Impuesto impuesto, Descuento descuento
    ) {
        this(UUID.randomUUID().toString(), nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, true);
        validarEstadoImpuesto(impuesto);
        validarEstadoDescuento(descuento);
    }

    //MÉTODOS:

    protected BigDecimal getPrecioBase(){
        return getValorCompra().multiply(
                (BigDecimal.ONE).add(getPorcentajeGanancia().divide(CIEN, 6, RoundingMode.HALF_UP))
        )
        .setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal precioBase) {
        if (getPorcentajeDescuento().compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        return precioBase.multiply(
                getPorcentajeDescuento().divide(CIEN, 6, RoundingMode.HALF_UP)
        )
        .setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularImpuesto(BigDecimal precioFinalSinImpuesto) {
        return precioFinalSinImpuesto.multiply(
                getPorcentajeImpuesto().divide(CIEN, 6, RoundingMode.HALF_UP)
        )
        .setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getValorFinalSinImpuesto(LocalDate fecha) {
        BigDecimal precioBase = getPrecioBase();
        return precioBase.subtract(calcularDescuento(precioBase));
    }

    protected BigDecimal calcularValorVenta(LocalDate fecha){
        BigDecimal valorFinalSinImpuesto = getValorFinalSinImpuesto(fecha);
        return valorFinalSinImpuesto.add(
                calcularImpuesto(valorFinalSinImpuesto)
        )
        .setScale(6, RoundingMode.HALF_UP);
    }

    //Se sobreescribe es Perecedero
    public void validarEstadoParaVenta(LocalDate fecha) {}

    //MÉTODOS MODIFICAR PRODUCTO:

    public void cambiarValorVentaPorPorcentaje(BigDecimal porcentajeGanancia) {
        validarPorcentajeGanancia(porcentajeGanancia);
        setPorcentajeGanancia(porcentajeGanancia);
    }

    public void cambiarNombreProducto(String nombre){
        validarNombre(nombre);
        setNombre(nombre);
    }

    public void cambiarValorCompra(BigDecimal valorNuevo){
        validarValorCompra(valorNuevo);
        setValorCompra(valorNuevo);
    }

    public void aumentarStock(int cantidad){
        validarStock(stock);
        this.stock = this.getStock() + cantidad;
    }

    public void reducirStock(int cantidad){
        if (cantidad<=0){
            throw new IllegalArgumentException("Cantidad de Producto a Retirar Invalida");
        }
        int stockTotal = this.getStock() - cantidad;
        if (stockTotal<0){
            throw new StockInsuficienteException("La Cantidad de Producto a Reducir es Mayor a la Cantidad Existente");
        }
        this.stock = stockTotal;
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

