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
        if (producto instanceof ProductoRopa ropa){
            return new DatosTotalesProductoRopaDTO(ropa.getCodigo(), ropa.getNombre(), ropa.getValorCompra(),
                    ropa.getPorcentajeGanancia(), ropa.getValorVenta(fecha), ropa.getStock(), ropa.getTalla());
        } else if (producto instanceof ProductoPerecedero perecedero){
            String estado;
            if (perecedero.estaVencido(fecha)){
                estado = "Vencido";
            } else {
                estado = "Disponible";
            }
            return new DatosTotalesProductoPerecederoDTO(perecedero.getCodigo(), perecedero.getNombre(),
                    perecedero.getValorCompra(), perecedero.getPorcentajeGanancia(), perecedero.getValorVenta(fecha),
                    perecedero.getStock(), perecedero.getFechaVencimiento(), estado);
        } else {
            throw new IllegalStateException("Tipo de Producto no soportado por el Sistema");
        }
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

