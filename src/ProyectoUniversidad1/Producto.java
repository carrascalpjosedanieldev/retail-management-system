package ProyectoUniversidad1;

public class Producto {//x LINEAS NETAS DE 160 LINEAS TOTALES

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
        if (nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Invalido");
        }
        if (valorCompra<=0) {
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
        if (stock<0){
            throw new IllegalArgumentException("Stock del Producto Invalido");
        }
        this.codigo = codigoSiguiente++;
        this.nombre = nombre;
        this.valorCompra = valorCompra;
        this.porcentajeGanancia = 20;
        this.valorVenta = this.valorCompra + (this.valorCompra*(this.porcentajeGanancia/100));
        this.stock = stock;
    }

    public Producto(String nombre, double valorCompra) throws IllegalArgumentException{
        if (nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Invalido");
        }
        if (valorCompra<=0) {
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
        this.codigo = codigoSiguiente++;
        this.nombre = nombre;
        this.valorCompra = valorCompra;
        this.porcentajeGanancia = 20;
        this.valorVenta = this.valorCompra + (this.valorCompra*(this.porcentajeGanancia/100));
        this.stock = 0;
    }

    //METODOS:

    public String describirProducto(){
        String descripcion;
        descripcion = String.format("Nombre del Producto:  %-10s Codigo:  %-4d Valor Compra:  %-12.2f Ganancia:  %3.0f%s Valor Venta:  %-12.2f Stock:  %-4d%n",
                getNombre(),getCodigo(),getValorCompra(),getPorcentajeGanancia(),"%   ", getValorVenta(),getStock());
        return descripcion;
    }

    //METODOS MODIFICAR PRODUCTO:

    public void cambiarValorVentaPorPorcentaje(double porcentajeGanancia) throws IllegalArgumentException{
        if (porcentajeGanancia<20 || porcentajeGanancia>100){
            throw new IllegalArgumentException("Porcentaje De Ganancia Invalido");
        }
        setPorcentajeGanancia(porcentajeGanancia);
        this.valorVenta = this.valorCompra + (this.valorCompra * (this.porcentajeGanancia / 100));
    }

    public void cambiarValorVentaPorPrecio(double precioDeseado) throws IllegalArgumentException{
        double porcentajeImplicito = ((precioDeseado - this.valorCompra) / this.valorCompra) * 100;
        if (porcentajeImplicito < 20 || porcentajeImplicito > 100) {
            throw new IllegalArgumentException("Precio De Venta No Cumple Las Reglas Necesarias");
        }
        setPorcentajeGanancia(porcentajeImplicito);
        this.valorVenta = precioDeseado;
    }

    public void cambiarNombreProducto(String nombre){
        if (nombre.isBlank()){
            System.out.println("ACCION RECHAZADA:\nNO se puede cambiar el Nombre del Producto " + getNombre());
        } else {
            System.out.print("Nombre Cambiado Con Exito:\nEl Producto:  " + getNombre());
            setNombre(nombre);
            System.out.println("   Ahora se llamara:  " + getNombre());
        }
    }

    public void cambiarValorCompra(double valorNuevo){
        if (valorNuevo<=0){
            System.out.println("ACCION RECHAZADA:\nNO se puede cambiar el precio del Producto " + getNombre());
        } else {
            setValorCompra(valorNuevo);
            this.valorVenta = this.valorCompra + (this.valorCompra * (this.porcentajeGanancia / 100));
            System.out.println("Valor de Compra Cambiado Con Exito:\nEl valor de compra del producto " + getNombre() +
                    " ahora es de:  $" + getValorCompra());
        }
    }

    public void actualizarStock(int cantidad){
        int stockTotal = (getStock()) + cantidad;
        setStock(stockTotal);
    }

    public void reducirStock(int cantidad){
        int stockTotal = (getStock()) - cantidad;
        setStock(stockTotal);
    }

}
