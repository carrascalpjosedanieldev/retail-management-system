package ProyectoUniversidad1;

public abstract class Producto {

    //ATRIBUTOS:

    private final int codigo;

    private static int codigoSiguiente = 1;

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

    public double getValorVenta(){
        return calcularValorVenta();
    }

    protected Producto(String nombre, double valorCompra, int stock) throws IllegalArgumentException{
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
        this.stock=stock;
        codigoSiguiente++;
    }

    public abstract String describirProducto();

    protected double calcularValorVenta(){
        return this.valorCompra + (this.valorCompra * (this.porcentajeGanancia / 100));
    }

    //METODOS MODIFICAR PRODUCTO:

    public void cambiarValorVentaPorPorcentaje(double porcentajeGanancia) throws IllegalArgumentException{
        if (porcentajeGanancia<20 || porcentajeGanancia>100){
            throw new IllegalArgumentException("Porcentaje De Ganancia Invalido");
        }
        setPorcentajeGanancia(porcentajeGanancia);
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

