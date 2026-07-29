package ProyectoPropio1.dominio.entidades;

import ProyectoPropio1.dominio.excepciones.StockInsuficienteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ItemCarrito {

    //ATRIBUTOS:

    private final ItemFacturable itemFacturable;

    private int cantidad;

    //GETTERS Y SETTERS:

    public ItemFacturable getItemFacturable() {
        return itemFacturable;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    //CONSTRUCTORES:

    private ItemCarrito(ItemFacturable itemFacturable, int cantidad) {
        if (itemFacturable == null){
            throw new IllegalArgumentException("Debe Haber un Item Valido para Agregar al Carrito");
        }
        if (cantidad<=0){
            throw new IllegalArgumentException("La Cantidad del Item Carrito Debe Ser Positiva");
        }
        this.itemFacturable = itemFacturable;
        this.cantidad = cantidad;
    }

    public static ItemCarrito crearNuevo(ItemFacturable itemFacturable, int cantidad){
        return new ItemCarrito(itemFacturable, cantidad);
    }

    //METODOS:

    public BigDecimal calcularSubtotal(LocalDate fecha) {
        BigDecimal valorProducto = this.itemFacturable.getValorVenta(fecha);
        valorProducto = valorProducto.multiply(new BigDecimal(this.cantidad));
        return valorProducto.setScale(2, RoundingMode.HALF_UP);
    }

    public void aumentarCantidad(int cantidadExtra) {
        if (cantidadExtra <= 0){
            throw new IllegalArgumentException("Cantidad a comprar Invalida");
        }
        this.cantidad += cantidadExtra;
    }

    public void reducirCantidad(int cantidadAReducir){
        if (cantidadAReducir <= 0){
            throw new IllegalArgumentException("Cantidad a Reducir Invalida");
        }
        int cantidadTotal = getCantidad() - cantidadAReducir;
        if (cantidadTotal <= 0){
            throw new StockInsuficienteException("La Cantidad a Reducir es Mayor o Igual a la Cantidad Existente");
        }
        setCantidad(cantidadTotal);
    }

}

