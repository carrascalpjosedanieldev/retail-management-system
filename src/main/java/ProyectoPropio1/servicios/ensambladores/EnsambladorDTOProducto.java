package ProyectoPropio1.servicios.ensambladores;

import ProyectoPropio1.dominio.Producto;
import ProyectoPropio1.dominio.ProductoPerecedero;
import ProyectoPropio1.dominio.ProductoRopa;
import ProyectoPropio1.dto.*;

import java.time.LocalDate;

public class EnsambladorDTOProducto {

    public EnsambladorDTOProducto() {
    }

    public DatosTotalesProductoDTO ensamblarDatosTotalesProducto(Producto producto, LocalDate fecha){
        return switch (producto) {
            case ProductoRopa productoRopa ->
                    new DatosTotalesProductoRopaDTO(productoRopa.getCodigo(), productoRopa.getNombre(), productoRopa.getValorCompra(),
                    productoRopa.getPorcentajeGanancia(), productoRopa.getValorVenta(fecha), productoRopa.getStock(), productoRopa.getTalla());
            case ProductoPerecedero productoPerecedero ->
                    new DatosTotalesProductoPerecederoDTO(productoPerecedero.getCodigo(), productoPerecedero.getNombre(),
                    productoPerecedero.getValorCompra(), productoPerecedero.getPorcentajeGanancia(), productoPerecedero.getValorVenta(fecha),
                    productoPerecedero.getStock(), productoPerecedero.getFechaVencimiento(), productoPerecedero.estaVencido(fecha));
            default -> throw new IllegalStateException("Tipo de Producto no soportado por el Sistema");
        };
    }

    public DatosVentaProductoDTO ensamblarDatosVentaProducto(Producto producto, LocalDate fecha){
        return switch (producto) {
            case ProductoRopa productoRopa ->
                    new DatosVentaProductoRopaDTO(productoRopa.getNombre(), productoRopa.getValorVenta(fecha), productoRopa.getTalla());
            case ProductoPerecedero productoPerecedero ->
                    new DatosVentaProductoPerecederoDTO(productoPerecedero.getNombre(), productoPerecedero.getValorVenta(fecha), productoPerecedero.getFechaVencimiento());
            default -> throw new IllegalStateException("Tipo de Producto no soportado por el Sistema");
        };
    }

}

