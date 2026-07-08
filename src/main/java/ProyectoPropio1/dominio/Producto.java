package ProyectoPropio1.dominio;

import ProyectoPropio1.dominio.enums.TipoItem;
import ProyectoPropio1.excepciones.StockInsuficienteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final Impuesto impuesto;

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

    public int getIdImpuesto() {
        return impuesto.getId();
    }

    @Override
    public BigDecimal getPorcentajeImpuesto(){return impuesto.getPorcentaje();}

    public Impuesto getImpuesto() {
        return impuesto;
    }

    public boolean isActivo(){
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    //CONSTRUCTOR:

    protected Producto(String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, Impuesto impuesto, boolean activo){
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
            throw new IllegalArgumentException("El Producto Debe tener Impuesto");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.valorCompra = valorCompra;
        this.porcentajeGanancia = porcentajeGanancia;
        this.stock = stock;
        this.impuesto = impuesto;
        this.activo = activo;
    }

    protected Producto(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, Impuesto impuesto){
        this(UUID.randomUUID().toString(), nombre, valorCompra, porcentajeGanancia, stock, impuesto, true);
    }

    //METODOS:

    protected BigDecimal calcularValorVenta(LocalDate fecha){
        BigDecimal factorGanancia = this.porcentajeGanancia.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal ganancia = this.valorCompra.multiply(factorGanancia);
        BigDecimal impuesto = calcularImpuesto(fecha);
        BigDecimal valorVenta = this.valorCompra.add(ganancia).add(impuesto);
        return valorVenta.setScale(4, RoundingMode.HALF_UP);
    }

    public void validarEstadoParaVenta(LocalDate fecha){
    }

    //METODOS MODIFICAR PRODUCTO:

    public void cambiarValorVentaPorPorcentaje(BigDecimal porcentajeGanancia) {
        if (porcentajeGanancia.compareTo(BigDecimal.ZERO) <= 0 || porcentajeGanancia.compareTo(new BigDecimal("100")) > 0) {
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

    public void activarProducto(){
        setActivo(true);
    }

    public void desactivarProducto(){
        setActivo(false);
    }

}

