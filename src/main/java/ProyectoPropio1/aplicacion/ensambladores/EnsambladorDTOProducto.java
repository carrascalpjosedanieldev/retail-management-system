package ProyectoPropio1.aplicacion.ensambladores;

import ProyectoPropio1.aplicacion.dto.*;
import ProyectoPropio1.dominio.entidades.Producto;
import ProyectoPropio1.dominio.entidades.ProductoPerecedero;
import ProyectoPropio1.dominio.entidades.ProductoRopa;
import ProyectoPropio1.aplicacion.dto.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOProducto {

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    private final EnsambladorDTODescuento ensambladorDTODescuento;

    private final EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;

    public EnsambladorDTOProducto(
            EnsambladorDTOImpuesto ensambladorDTOImpuesto, EnsambladorDTODescuento ensambladorDTODescuento,
            EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento
    ) {
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
        this.ensambladorDTOPoliticaVencimiento = ensambladorDTOPoliticaVencimiento;
    }

    public DatosTotalesProductoDTO ensamblarDatosTotalesProducto(Producto producto, LocalDate fecha){
        String disponible;
        if (producto.isActivo()){
            disponible = "Disponible";
        } else {
            disponible = "NO Disponible";
        }
        if (producto instanceof ProductoRopa ropa){
            ImpuestoDTO datosImpuesto = this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(
                    ropa.getImpuesto()
            );
            DescuentoDTO datosDescuento = this.ensambladorDTODescuento.ensamblarDatosDescuento(
                    ropa.getDescuento()
            );
            return new DatosTotalesProductoRopaDTO(
                    ropa.getCodigo(), ropa.getNombre(), ropa.getValorCompra(), ropa.getPorcentajeGanancia(),
                    ropa.getValorVenta(fecha), ropa.getStock(), datosImpuesto, datosDescuento, ropa.getTalla(),
                    disponible
            );
        } else if (producto instanceof ProductoPerecedero perecedero){
            ImpuestoDTO datosImpuesto = this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(
                    perecedero.getImpuesto()
            );
            DescuentoDTO datosDescuento = this.ensambladorDTODescuento.ensamblarDatosDescuento
                    (perecedero.getDescuento()
                    );
            PoliticaVencimientoDTO datosPoliticaVencimiento = this.ensambladorDTOPoliticaVencimiento.ensamblarDatosPoliticaVencimiento(
                    perecedero.getPoliticaVencimiento()
            );
            String estado;
            if (perecedero.estaVencido(fecha)){
                estado = "Vencido";
            } else {
                estado = "NO Vencido";
            }
            return new DatosTotalesProductoPerecederoDTO(
                    perecedero.getCodigo(), perecedero.getNombre(), perecedero.getValorCompra(),
                    perecedero.getPorcentajeGanancia(), perecedero.getValorVenta(fecha), perecedero.getStock(),
                    datosImpuesto, datosDescuento, perecedero.getFechaVencimiento(), datosPoliticaVencimiento,
                    estado, disponible
            );
        } else {
            throw new IllegalStateException("Tipo de Producto no soportado por el Sistema");
        }
    }

    public List<DatosTotalesProductoRopaDTO> ensamblarDetalleProductosRopa(List<Producto> productosRopa, LocalDate fecha){
        List<DatosTotalesProductoRopaDTO> datosProductosRopa = new ArrayList<>();
        for (Producto producto:productosRopa){
            DatosTotalesProductoRopaDTO productoResumen = (DatosTotalesProductoRopaDTO) this.ensamblarDatosTotalesProducto(producto, fecha);
            datosProductosRopa.add(productoResumen);
        }
        return datosProductosRopa;
    }

    public List<DatosTotalesProductoPerecederoDTO> ensamblarDetalleProductosPerecedero(List<Producto> productosRopa, LocalDate fecha){
        List<DatosTotalesProductoPerecederoDTO> datosProductosRopa = new ArrayList<>();
        for (Producto producto:productosRopa){
            DatosTotalesProductoPerecederoDTO productoResumen = (DatosTotalesProductoPerecederoDTO) this.ensamblarDatosTotalesProducto(producto, fecha);
            datosProductosRopa.add(productoResumen);
        }
        return datosProductosRopa;
    }

    public List<ProductoResumenDTO> ensamblarDetalleProductosResumen(List<Producto> productos, LocalDate fecha){
        List<ProductoResumenDTO> resumenProductos = new ArrayList<>();
        for (Producto producto:productos){
            ProductoResumenDTO productoResumen = new ProductoResumenDTO(
                    producto.getCodigo(), producto.getNombre(), producto.getValorVenta(fecha),
                    producto.getStock(), producto.isActivo()
            );
            resumenProductos.add(productoResumen);
        }
        return resumenProductos;
    }

}

