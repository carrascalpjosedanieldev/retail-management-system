package ProyectoPropio1.dominio;

import ProyectoPropio1.excepciones.StockInsuficienteException;

import java.time.LocalDate;

public abstract class Producto {

    //ATRIBUTOS:

    private final int codigo;

    private String nombre;

    private double valorCompra;

    private double porcentajeGanancia;

    private int stock;

    //GETTERS Y SETTERS:

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
    protected void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getValorCompra() {
        return valorCompra;
    }
    protected void setValorCompra(double valorCompra) {
        this.valorCompra = valorCompra;
    }

    public double getPorcentajeGanancia() {
        return porcentajeGanancia;
    }
    protected void setPorcentajeGanancia(double porcentajeGanancia) {
        this.porcentajeGanancia = porcentajeGanancia;
    }

    public int getStock() {
        return stock;
    }

    protected void setStock(int stock) {
        this.stock = stock;
    }

    public double getValorVenta(LocalDate fecha){
        return calcularValorVenta(fecha);
    }

    //CONSTRUCTOR:

    protected Producto(int codigo, String nombre, double valorCompra, int stock){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Invalido");
        }
        if (valorCompra<=0) {
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
        if (stock<0){
            throw new IllegalArgumentException("Stock del Producto Invalido");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.valorCompra = valorCompra;
        this.porcentajeGanancia = 20;
        this.stock=stock;
    }

    //METODOS:

    protected double calcularValorVenta(LocalDate fecha){
        return this.valorCompra + (this.valorCompra * (this.porcentajeGanancia / 100));
    }

    public void validarEstadoParaVenta(LocalDate fecha){
    }

    //METODOS MODIFICAR PRODUCTO:

    protected void cambiarValorVentaPorPorcentaje(double porcentajeGanancia){
        if (porcentajeGanancia<20 || porcentajeGanancia>100){
            throw new IllegalArgumentException("Porcentaje De Ganancia Invalido");
        }
        setPorcentajeGanancia(porcentajeGanancia);
    }

    protected void cambiarNombreProducto(String nombre){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        setNombre(nombre);
    }

    protected void cambiarValorCompra(double valorNuevo){
        if (valorNuevo<=0){
            throw new IllegalArgumentException("Valor Negativo");
        }
        setValorCompra(valorNuevo);
    }

    protected void aumentarStock(int cantidad){
        if (cantidad<0){
            throw new IllegalArgumentException("Cantidad Negativa");
        }
        int stockTotal = (getStock()) + cantidad;
        setStock(stockTotal);
    }

    protected void reducirStock(int cantidad){
        if (cantidad<0){
            throw new IllegalArgumentException("Cantidad Negativa");
        }
        int stockTotal = (getStock()) - cantidad;
        if (stockTotal<0){
            throw new StockInsuficienteException("La Cantidad A Reducir Es Mayor A La Cantidad Existente");
        }
        setStock(stockTotal);
    }

}

