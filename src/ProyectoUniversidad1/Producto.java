package ProyectoUniversidad1;

public class Producto {//125 LINEAS NETAS DE 160 LINEAS TOTALES

    //ATRIBUTOS:

    private final int CODIGO;

    private static int codigoSiguiente = 1;

    private String nombre;

    private double valorCompra;

    private double porcentajeGanancia;

    private double valorVenta;

    private int stock;

    //GETTERS Y SETTERS:

    public int getCODIGO() {
        return CODIGO;
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

    public Producto(String nombre, double valorCompra, int stock) {
        if (nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Invalido");
        }
        if (valorCompra<=0) {
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
        if (stock<0){
            throw new IllegalArgumentException("Stock del Producto Invalido");
        }
        this.CODIGO = codigoSiguiente++;
        this.nombre = nombre;
        this.valorCompra = valorCompra;
        this.porcentajeGanancia = 20;
        this.valorVenta = this.valorCompra + (this.valorCompra*(this.porcentajeGanancia/100));
        this.stock = stock;
    }

    public Producto(String nombre, double valorCompra) {
        if (nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Producto Invalido");
        }
        if (valorCompra<=0) {
            throw new IllegalArgumentException("Valor de Compra del Producto Invalido");
        }
        this.CODIGO = codigoSiguiente++;
        this.nombre = nombre;
        this.valorCompra = valorCompra;
        this.porcentajeGanancia = 20;
        this.valorVenta = this.valorCompra + (this.valorCompra*(this.porcentajeGanancia/100));
        this.stock = 0;
    }

    //METODOS:

    public void describirProducto(){
        System.out.printf("%s %-10s %s %-4d %s %-12.2f %s %3.0f%s %s %-12.2f %s %-4d%n","Nombre del Producto: ",
                getNombre(),"Codigo: ",getCODIGO(), "Valor Compra: ",getValorCompra(), "Ganancia: ",
                getPorcentajeGanancia(),"%","   Valor Venta: ", getValorVenta(),"Stock: ",getStock());
    }

    //METODOS MODIFICAR PRODUCTO:

    public void cambiarValorVentaPorPorcentaje(double porcentajeGanancia){
        if (porcentajeGanancia<20 || porcentajeGanancia>100){
            System.out.println("ACCION RECHAZADA:\nEl porcentaje de ganacia debe estar entre 20 y 100");
        } else {
            setPorcentajeGanancia(porcentajeGanancia);
            this.valorVenta = this.valorCompra + (this.valorCompra * (this.porcentajeGanancia / 100));
            System.out.println("Precio Cambiado Con Exito:\nEl Precio del Producto " + getNombre() + " ahora es de:  $" + getValorVenta());
        }
    }

    public void cambiarValorVentaPorPrecio(double precioDeseado) {
        double porcentajeImplicito = ((precioDeseado - this.valorCompra) / this.valorCompra) * 100;
        if (porcentajeImplicito < 20 || porcentajeImplicito > 100) {
            System.out.println("ACCION RECHAZADA:");
            System.out.println("El precio deseado implica un margen de " + String.format("%.2f", porcentajeImplicito)
                    + "% fuera del rango permitido (20-100%). Por tanto, Accion Denegada");
        } else {
            setPorcentajeGanancia(porcentajeImplicito);
            this.valorVenta = precioDeseado;
            System.out.println("Precio Cambiado Con Exito:\nEl Precio del Producto " + getNombre() + " ahora es " +
                    "de:  $" + getValorVenta());
        }
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
