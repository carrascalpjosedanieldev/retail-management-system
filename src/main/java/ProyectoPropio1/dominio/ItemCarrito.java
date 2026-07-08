package ProyectoPropio1.dominio;

import ProyectoPropio1.excepciones.StockInsuficienteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ItemCarrito {

    //ATRIBUTOS:

    private ItemFacturable itemFacturable;

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

    public ItemCarrito(ItemFacturable itemFacturable, int cantidad) {
        this.itemFacturable = itemFacturable;
        this.cantidad = cantidad;
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
            throw new StockInsuficienteException("La Cantidad de Producto a Reducir es Mayor o Igual a la Cantidad Existente");
        }
        setCantidad(cantidadTotal);
    }

}
