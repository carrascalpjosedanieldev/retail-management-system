package RetailManagementSystem.dominio.entidades.ventas;

import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
import RetailManagementSystem.dominio.entidades.comercial.Stockeable;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.StockInsuficienteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ItemCarrito {

    //ATRIBUTOS:

    private final ItemFacturable itemFacturable;

    private final BigDecimal valorVentaUnidad;

    private int cantidad;

    //GETTERS Y SETTERS:

    public ItemFacturable getItemFacturable() {
        return itemFacturable;
    }

    public BigDecimal getValorVentaUnidad() {
        return valorVentaUnidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    //CONSTRUCTORES:

    private ItemCarrito(ItemFacturable itemFacturable, BigDecimal valorVentaUnidad, int cantidad) {
        if (itemFacturable == null){
            throw new IllegalArgumentException("Debe Haber un Item Valido para Agregar al Carrito");
        }
        if (cantidad<=0){
            throw new IllegalArgumentException("La Cantidad del Item Carrito Debe Ser Positiva");
        }
        this.itemFacturable = itemFacturable;
        verificarStockDisponible(cantidad);
        this.valorVentaUnidad = valorVentaUnidad.setScale(6, RoundingMode.HALF_UP);
        this.cantidad = cantidad;
    }

    public static ItemCarrito crearNuevo(ItemFacturable itemFacturable, BigDecimal valorVentaUnidad, int cantidad){
        return new ItemCarrito(itemFacturable, valorVentaUnidad, cantidad);
    }

    //VALIDACIONES:

    private void verificarStockDisponible(int cantidadDeseada) {
        if (this.itemFacturable instanceof Stockeable itemConStock) {
            itemConStock.validarStockDisponible(cantidadDeseada);
        }
    }

    //MÉTODOS:

    public void aumentarCantidad(int cantidadExtra) {
        if (cantidadExtra <= 0){
            throw new IllegalArgumentException("Cantidad a comprar Invalida");
        }
        int nuevaCantidad = this.cantidad + cantidadExtra;
        verificarStockDisponible(nuevaCantidad);
        this.cantidad = nuevaCantidad;
    }

    public void reducirCantidad(int cantidadAReducir){
        if (cantidadAReducir <= 0){
            throw new IllegalArgumentException("Cantidad a Reducir Invalida");
        }
        int cantidadTotal = getCantidad() - cantidadAReducir;
        if (cantidadTotal < 0){
            throw new StockInsuficienteException("La Cantidad a Reducir es Mayor a la Cantidad Existente");
        }
        this.cantidad = cantidadTotal;
    }

    public BigDecimal calcularSubtotal() {
        return this.valorVentaUnidad.multiply(new BigDecimal(this.cantidad))
                .setScale(6, RoundingMode.HALF_UP);
    }

}//===================================================================================================================//

