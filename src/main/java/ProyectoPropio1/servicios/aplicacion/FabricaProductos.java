package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Descuento;
import ProyectoPropio1.dominio.Impuesto;
import ProyectoPropio1.dominio.ProductoPerecedero;
import ProyectoPropio1.dominio.ProductoRopa;
import ProyectoPropio1.dominio.enums.Talla;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FabricaProductos {

    //ATRIBUTOS:

    private final ServicioImpuestos servicioImpuestos;

    private final ServicioDescuentos servicioDescuentos;

    private record ComponentesComunes(Impuesto impuesto, Descuento descuento) {}

    //CONSTRUCTORES:

    public FabricaProductos(ServicioImpuestos servicioImpuestos, ServicioDescuentos servicioDescuentos) {
        this.servicioImpuestos = servicioImpuestos;
        this.servicioDescuentos = servicioDescuentos;
    }

    //METODOS:

    private ComponentesComunes obtenerYValidarComponentes(int idImpuesto, int idDescuento) {
        Impuesto impuesto = this.servicioImpuestos.obtenerImpuesto(idImpuesto);
        if (!impuesto.isActivo()) {
            throw new IllegalArgumentException("No se puede asignar el Impuesto -" + impuesto.getNombre() + "- porque se encuentra Inactivo.");
        }
        Descuento descuento = this.servicioDescuentos.obtenerDescuento(idDescuento);
        if (!descuento.isActivo()) {
            throw new IllegalArgumentException("No se puede asignar el Descuento -" + descuento.getNombre() + "- porque se encuentra Inactivo.");
        }
        return new ComponentesComunes(impuesto, descuento);
    }

    public ProductoRopa fabricarProductoRopa(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                                             int stock, int idImpuesto, int idDescuento, String tallaString) {
        ComponentesComunes componentes = obtenerYValidarComponentes(idImpuesto, idDescuento);
        Talla talla;
        try {
            talla = Talla.valueOf(tallaString.toUpperCase());
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("La talla ingresada no está entre las opciones (Usa S, M, L o XL).");
        }
        return ProductoRopa.crearNuevo(nombre, valorCompra, porcentajeGanancia, stock, componentes.impuesto(), componentes.descuento(), talla);
    }

    public ProductoPerecedero fabricarProductoPerecedero(String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
                                                         int stock, int idImpuesto, int idDescuento,
                                                         LocalDate fechaVencimiento, LocalDate fechaActual) {
        ComponentesComunes componentes = obtenerYValidarComponentes(idImpuesto, idDescuento);
        if (fechaVencimiento.isBefore(fechaActual)){
            throw new IllegalArgumentException("NO se puede Registrar el Producto porque ya está Vencido");
        }
        return ProductoPerecedero.crearNuevo(nombre, valorCompra, porcentajeGanancia, stock, componentes.impuesto(), componentes.descuento(), fechaVencimiento);
    }

}
