package RetailManagementSystem.dominio.entidades;

import RetailManagementSystem.dominio.excepciones.StockInsuficienteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class Carrito {

    //ATRIBUTOS:

    private final Map<String,ItemCarrito> carritoFinal;

    //GETTERS Y SETTERS:

    public Map<String,ItemCarrito> getItems(){
        return Map.copyOf(this.carritoFinal);
    }

    //CONSTRUCTOR:

    private Carrito() {
        this.carritoFinal = new LinkedHashMap<>();
    }

    public static Carrito crearNueva(){
        return new Carrito();
    }

    //MÉTODOS:

    public void agregarProducto(Producto producto, int cantidad){
        if (this.carritoFinal.containsKey(producto.getCodigo())){
            int cantidadTotalSolicitada = this.carritoFinal.get(producto.getCodigo()).getCantidad() + cantidad;
            if (cantidadTotalSolicitada > producto.getStock()){
                throw new StockInsuficienteException("Stock del Producto -" + producto.getNombre() + "- Insuficiente\n" +
                        "Cantidad Solicitada:  " + cantidadTotalSolicitada + ", Cantidad Existente:  " +
                        producto.getStock());
            }
            this.carritoFinal.get(producto.getCodigo()).aumentarCantidad(cantidad);
            return;
        }
        if (cantidad > producto.getStock()){
            throw new StockInsuficienteException("Stock del Producto -" + producto.getNombre() + "- Insuficiente\n" +
                    "Cantidad Solicitada:  " + cantidad + ", Cantidad Existente:  " + producto.getStock());
        }
        ItemCarrito itemCarrito = ItemCarrito.crearNuevo(producto, cantidad);
        this.carritoFinal.put(producto.getCodigo(), itemCarrito);
    }

    public void reducirCantidadProducto(String codigoProducto, int cantidadAReducir){
        if (!this.carritoFinal.containsKey(codigoProducto)){
            throw new IllegalArgumentException("NO tienes ese Producto en el Carrito");
        }
        this.carritoFinal.get(codigoProducto).reducirCantidad(cantidadAReducir);
    }

    public  void  eliminarProducto(String codigoProducto){
        if (!this.carritoFinal.containsKey(codigoProducto)){
            throw new IllegalArgumentException("NO tienes ese Producto en el Carrito");
        }
        this.carritoFinal.remove(codigoProducto);
    }

    public void agregarServicio(Servicio servicio, int cantidad){
        if (this.carritoFinal.containsKey(servicio.getCodigo())){
            this.carritoFinal.get(servicio.getCodigo()).aumentarCantidad(cantidad);
            return;
        }
        ItemCarrito itemCarrito = ItemCarrito.crearNuevo(servicio, cantidad);
        this.carritoFinal.put(servicio.getCodigo(), itemCarrito);
    }

    public void reducirCantidadServicio(String codigoServicio, int cantidadAReducir){
        if (!this.carritoFinal.containsKey(codigoServicio)){
            throw new IllegalArgumentException("NO tienes ese Servicio en el Carrito");
        }
        this.carritoFinal.get(codigoServicio).reducirCantidad(cantidadAReducir);
    }

    public void eliminarServicio(String codigoServicio){
        if (!this.carritoFinal.containsKey(codigoServicio)){
            throw new IllegalArgumentException("NO tienes ese Servicio en el Carrito");
        }
        this.carritoFinal.remove(codigoServicio);
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
        this.carritoFinal.clear();
    }

}//===================================================================================================================//

