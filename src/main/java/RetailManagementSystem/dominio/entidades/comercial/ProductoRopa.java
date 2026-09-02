package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.Talla;

import java.math.BigDecimal;

public final class ProductoRopa extends Producto{

    //ATRIBUTOS:

    private final Talla talla;

    //GETTERS Y SETTERS:

    public Talla getTalla() {
        return talla;
    }

    //CONSTRUCTOR:

    private ProductoRopa(
            String  codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
            Integer stock, Impuesto impuesto, Descuento descuento, Boolean activo, Talla talla
    ) {
        super(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, activo);
        if (talla == null){
            throw new IllegalArgumentException("La Talla de la Prenda es Obligatoria");
        }
        this.talla=talla;
    }

    public static ProductoRopa reconstruirDesdeBD(
            String  codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
            Integer stock, Impuesto impuesto, Descuento descuento, Boolean activo, Talla talla
    ) {
        return new ProductoRopa(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, activo, talla);
    }

    private ProductoRopa(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, Integer stock,
            Impuesto impuesto, Descuento descuento, Talla talla
    ) {
        super(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento);
        this.talla = talla;
    }

    public static ProductoRopa crearNuevo(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
            Integer stock, Impuesto impuesto, Descuento descuento, Talla talla
    ) {
        return new ProductoRopa(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, talla);
    }

}//===================================================================================================================//

