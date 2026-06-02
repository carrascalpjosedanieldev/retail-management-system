package ProyectoUniversidad1;

public class Producto {//x LINEAS NETAS DE 154 LINEAS TOTALES

    //ATRIBUTOS:

    private final int codigo;

    private static int codigoSiguiente = 1;

    private String nombre;

    private double valorCompra;

    private double porcentajeGanancia;

    private double valorVenta;

    private int stock;

    //GETTERS Y SETTERS:

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    private double getValorCompra() {
        return valorCompra;
    }
    private void setValorCompra(double valorCompra) {
        this.valorCompra = valorCompra;
    }

    public double getPorcentajeGanancia() {
        return porcentajeGanancia;
    }
    private void setPorcentajeGanancia(double porcentajeGanancia) {
        this.porcentajeGanancia = porcentajeGanancia;
    }

    public double getValorVenta() {
        return valorVenta;
    }

    public int getStock() {
        return stock;
    }

    private void setStock(int stock) {
        this.stock = stock;
    }

    //CONSTRUCTORES:

    public Producto(String nombre, double valorCompra, int stock) throws IllegalArgumentException{
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Invalido");
        }
        if (valorCompra<=0) {
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
        if (stock<0){
            throw new IllegalArgumentException("Stock del Producto Invalido");
        }
        this.codigo = codigoSiguiente;
        this.nombre = nombre;
        this.valorCompra = valorCompra;
        this.porcentajeGanancia = 20;
        this.valorVenta = calcularValorVenta();
        this.stock = stock;
        codigoSiguiente++;
    }

    //METODOS DE PRODUCTO:

    public String describirProducto(){
        String descripcion;
        descripcion = String.format("Nombre del Producto:  %-10s Codigo:  %-4d Valor Compra:  %-12.2f Ganancia:  %3.0f%s Valor Venta:  %-12.2f Stock:  %-4d%n",
                getNombre(),getCodigo(),getValorCompra(),getPorcentajeGanancia(),"%   ", getValorVenta(),getStock());
        return descripcion;
    }

    //METODOS PRIVADOS:

    private double calcularValorVenta(){
        return this.valorCompra + (this.valorCompra * (this.porcentajeGanancia / 100));
    }

    //METODOS MODIFICAR PRODUCTO:

    public void cambiarValorVentaPorPorcentaje(double porcentajeGanancia) throws IllegalArgumentException{
        if (porcentajeGanancia<20 || porcentajeGanancia>100){
            throw new IllegalArgumentException("Porcentaje De Ganancia Invalido");
        }
        setPorcentajeGanancia(porcentajeGanancia);
        this.valorVenta = calcularValorVenta();
    }

    public void cambiarValorVentaPorPrecio(double precioDeseado) throws IllegalArgumentException{
        double porcentajeImplicito = ((precioDeseado - this.valorCompra) / this.valorCompra) * 100;
        if (porcentajeImplicito < 20 || porcentajeImplicito > 100) {
            throw new IllegalArgumentException("Precio De Venta No Cumple Las Reglas Necesarias");
        }
        setPorcentajeGanancia(porcentajeImplicito);
        this.valorVenta = precioDeseado;
    }

    public void cambiarNombreProducto(String nombre) throws IllegalArgumentException{
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        setNombre(nombre);
    }

    public void cambiarValorCompra(double valorNuevo) throws IllegalArgumentException{
        if (valorNuevo<=0){
            throw new IllegalArgumentException("Valor Negativo");
        }
        setValorCompra(valorNuevo);
        this.valorVenta = calcularValorVenta();
    }

    public void aumentarStock(int cantidad) throws IllegalArgumentException{
        if (cantidad<0){
            throw new IllegalArgumentException("Cantidad Negativa");
        }
        int stockTotal = (getStock()) + cantidad;
        setStock(stockTotal);
    }

    public void reducirStock(int cantidad) throws IllegalArgumentException{
        if (cantidad<0){
            throw new IllegalArgumentException("Cantidad Negativa");
        }
        int stockTotal = (getStock()) - cantidad;
        if (stockTotal<0){
            throw new IllegalArgumentException("La Cantidad A Reducir Es Mayor A La Cantidad Existente");
        }
        setStock(stockTotal);
    }

}

