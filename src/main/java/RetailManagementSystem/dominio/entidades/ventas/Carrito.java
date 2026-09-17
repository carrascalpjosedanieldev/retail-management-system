package RetailManagementSystem.dominio.entidades.ventas;

import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
import RetailManagementSystem.dominio.entidades.comercial.Stockeable;
import RetailManagementSystem.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class Carrito {

    //ATRIBUTOS:

    private final Map<String, ItemCarrito> itemsCarrito;

    //GETTERS Y SETTERS:

    public Map<String,ItemCarrito> getItems(){
        return Map.copyOf(this.itemsCarrito);
    }

    //CONSTRUCTOR:

    private Carrito() {
        this.itemsCarrito = new LinkedHashMap<>();
    }

    public static Carrito crearNuevo(){
        return new Carrito();
    }

    //VALIDACIONES:

    private void validarExistenciaItem(String codigo, TipoItem tipoItem){
        if (!this.itemsCarrito.containsKey(codigo)){
            throw new IllegalArgumentException("NO tienes ese " + tipoItem + " en el Carrito");
        }
    }

    private void validarCantidad(int cantidad){
        if (cantidad <= 0){
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
    }

    //MÉTODOS:

    public void agregarItem(ItemFacturable item, int cantidad){
        validarCantidad(cantidad);
        int cantidadTotal = consultarCantidadExistente(item.getCodigo()) + cantidad;
        if (item instanceof Stockeable itemConStock) {
            itemConStock.validarStockDisponible(cantidadTotal);
        }
        this.itemsCarrito.compute(item.getCodigo(), (codigo, existente) -> {
            if (existente != null) {
                existente.aumentarCantidad(cantidad);
                return existente;
            }
            return ItemCarrito.crearNuevo(item, cantidad);
        });
    }

    private int consultarCantidadExistente(String codigo) {
        ItemCarrito existente = this.itemsCarrito.get(codigo);
        return existente != null ? existente.getCantidad() : 0;
    }

    public void reducirCantidadItem(String codigo, int cantidadAReducir, TipoItem tipoItem){
        validarExistenciaItem(codigo, tipoItem);
        validarCantidad(cantidadAReducir);
        ItemCarrito item = this.itemsCarrito.get(codigo);
        item.reducirCantidad(cantidadAReducir);
        if (item.getCantidad() == 0){
            this.itemsCarrito.remove(codigo);
        }
    }

    public void eliminarItem(String codigo, TipoItem tipoItem){
        validarExistenciaItem(codigo, tipoItem);
        this.itemsCarrito.remove(codigo);
    }

    public BigDecimal calcularTotal(LocalDate fecha) {
        BigDecimal total = BigDecimal.ZERO;
        for(ItemCarrito item : this.getItems().values()) {
            BigDecimal valorItem = item.calcularSubtotal(fecha);
            total = total.add(valorItem) ;
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public void vaciarCarrito() {
        this.itemsCarrito.clear();
    }

}//===================================================================================================================//

