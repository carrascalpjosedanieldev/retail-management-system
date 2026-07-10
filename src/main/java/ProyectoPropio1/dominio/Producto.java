package ProyectoPropio1.dominio;

import ProyectoPropio1.dominio.enums.TipoItem;
import ProyectoPropio1.excepciones.StockInsuficienteException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static ProyectoPropio1.dominio.enums.TipoItem.PRODUCTO;

public abstract class Producto implements ItemFacturable{

    //ATRIBUTOS:

    private final String codigo;

    private String nombre;

    private BigDecimal valorCompra;

    private BigDecimal porcentajeGanancia;

    private int stock;

    private Impuesto impuesto;

    private Descuento descuento;

    private boolean activo;

    protected static final BigDecimal CIEN = new BigDecimal("100");

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
        this.nombre = nombre;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }
    protected void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public BigDecimal getPorcentajeGanancia() {
        return porcentajeGanancia;
    }
    protected void setPorcentajeGanancia(BigDecimal porcentajeGanancia) {
        this.porcentajeGanancia = porcentajeGanancia;
    }

    public int getStock() {
        return stock;
    }
    protected void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public BigDecimal getValorVenta(LocalDate fecha){
        return calcularValorVenta(fecha);
    }

    public Impuesto getImpuesto() {
        return impuesto;
    }
    private void setImpuesto(Impuesto impuesto) {
        this.impuesto = impuesto;
    }

    public int getIdImpuesto() {
        return impuesto.getId();
    }

    @Override
    public BigDecimal getPorcentajeImpuesto(){return impuesto.getPorcentaje();}

    public Descuento getDescuento(){
        return descuento;
    }
    private void setDescuento(Descuento descuento) {
        this.descuento = descuento;
    }

    public int getIdDescuento(){
        return descuento.getId();
    }

    @Override
    public BigDecimal getPorcentajeDescuento(){
        return descuento.getPorcentaje();
    }

    public boolean isActivo(){
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    //CONSTRUCTOR:

    protected Producto(String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                       int stock, Impuesto impuesto, Descuento descuento, boolean activo){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Invalido");
        }
        if (valorCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
        if (stock<0){
            throw new IllegalArgumentException("Stock del Producto Invalido");
        }
        if (impuesto==null){
            throw new IllegalArgumentException("El Producto Debe Tener Impuesto");
        }
        if (descuento==null){
            throw new IllegalArgumentException("El Producto Debe Tener Descuento");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.valorCompra = valorCompra;
        this.porcentajeGanancia = porcentajeGanancia;
        this.descuento = descuento;
        this.stock = stock;
        this.impuesto = impuesto;
        this.activo = activo;
    }

    protected Producto(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock,
                       Impuesto impuesto, Descuento descuento){
        this(UUID.randomUUID().toString(), nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, true);
    }

    //METODOS:

    protected abstract BigDecimal calcularValorVenta(LocalDate fecha);

    public abstract void validarEstadoParaVenta(LocalDate fecha);

    //METODOS MODIFICAR PRODUCTO:

    public void cambiarValorVentaPorPorcentaje(BigDecimal porcentajeGanancia) {
        if (porcentajeGanancia.compareTo(BigDecimal.ZERO) <= 0 || porcentajeGanancia.compareTo(CIEN) > 0) {
            throw new IllegalArgumentException("Porcentaje de Ganancia del Producto Inválido");
        }
        setPorcentajeGanancia(porcentajeGanancia);
    }

    public void cambiarNombreProducto(String nombre){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Vacio");
        }
        setNombre(nombre);
    }

    public void cambiarValorCompra(BigDecimal valorNuevo){
        if (valorNuevo.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
        setValorCompra(valorNuevo);
    }

    public void aumentarStock(int cantidad){
        if (cantidad<0){
            throw new IllegalArgumentException("Cantidad de Producto a Ingresar Invalida");
        }
        int stockTotal = (getStock()) + cantidad;
        setStock(stockTotal);
    }

    public void reducirStock(int cantidad){
        if (cantidad<0){
            throw new IllegalArgumentException("Cantidad de Producto a Retirar Invalida");
        }
        int stockTotal = (getStock()) - cantidad;
        if (stockTotal<0){
            throw new StockInsuficienteException("La Cantidad de Producto a Reducir es Mayor a la Cantidad Existente");
        }
        setStock(stockTotal);
    }

    public void cambiarImpuesto(Impuesto impuesto){
        if (!impuesto.isActivo()){
            throw new IllegalArgumentException("El Impuesto que le quieres poner al Producto esta Inactivo");
        }
        setImpuesto(impuesto);
    }

    public void cambiarDescuento(Descuento descuento){
        if (!descuento.isActivo()){
            throw new IllegalArgumentException("El Descuento que le quieres poner al Producto esta Inactivo");
        }
        setDescuento(descuento);
    }

    public void activarProducto(){
        if (isActivo()){
            throw new IllegalArgumentException("El Producto ya esta Activo");
        }
        setActivo(true);
    }

    public void desactivarProducto(){
        if (!isActivo()){
            throw new IllegalArgumentException("El Producto ya esta Inactivo");
        }
        setActivo(false);
    }

}

