package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.enums.TipoProducto;

import java.math.BigDecimal;

public final class ProductoRopa extends Producto{

    //ATRIBUTOS:

    private final Talla talla;

    //GETTERS:

    public Talla getTalla() {
        return talla;
    }

    //VALIDACIONES:

    private void validarTalla(Talla talla){
        if (talla == null){
            throw new IllegalArgumentException("La Talla de la Prenda es Obligatoria");
        }
    }

    //CONSTRUCTOR:

    private ProductoRopa(
            String  codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
            Integer stock, Impuesto impuesto, Descuento descuento, Boolean activo, Talla talla
    ) {
        super(codigo, nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, activo, TipoProducto.ROPA);
        validarTalla(talla);
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
        super(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, TipoProducto.ROPA);
        validarTalla(talla);
        this.talla = talla;
    }

    public static ProductoRopa crearNuevo(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
            Integer stock, Impuesto impuesto, Descuento descuento, Talla talla
    ) {
        return new ProductoRopa(nombre, valorCompra, porcentajeGanancia, stock, impuesto, descuento, talla);
    }

}//===================================================================================================================//

