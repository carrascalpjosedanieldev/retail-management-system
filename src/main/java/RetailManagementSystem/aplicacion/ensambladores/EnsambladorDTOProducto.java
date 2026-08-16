package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.comercial.*;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;

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
                    ropa.isActivo()
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
            return new DatosTotalesProductoPerecederoDTO(
                    perecedero.getCodigo(), perecedero.getNombre(), perecedero.getValorCompra(),
                    perecedero.getPorcentajeGanancia(), perecedero.getValorVenta(fecha), perecedero.getStock(),
                    datosImpuesto, datosDescuento, perecedero.getFechaVencimiento(), datosPoliticaVencimiento,
                    perecedero.isActivo(), perecedero.estaVencido(fecha)
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

    public List<DatosTotalesProductoPerecederoDTO> ensamblarDetalleProductosPerecedero(List<Producto> productosRopa){
        LocalDate fecha = LocalDate.now();
        List<DatosTotalesProductoPerecederoDTO> datosProductosRopa = new ArrayList<>();
        for (Producto producto:productosRopa){
            DatosTotalesProductoPerecederoDTO productoResumen = (DatosTotalesProductoPerecederoDTO) this.ensamblarDatosTotalesProducto(producto, fecha);
            datosProductosRopa.add(productoResumen);
        }
        return datosProductosRopa;
    }

    public ProductoResumenDTO ensamblarProductoResumen(Producto producto, LocalDate fecha){
        return new ProductoResumenDTO(
                producto.getCodigo(), producto.getNombre(), producto.getValorVenta(fecha),
                producto.getStock(), producto.isActivo()
        );
    }

    public List<ProductoResumenDTO> ensamblarDetalleProductosResumen(List<Producto> productos, LocalDate fecha){
        List<ProductoResumenDTO> resumenProductos = new ArrayList<>();
        for (Producto producto:productos){
            ProductoResumenDTO productoResumen = ensamblarProductoResumen(producto, fecha);
            resumenProductos.add(productoResumen);
        }
        return resumenProductos;
    }

}//===================================================================================================================//

