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

    //ATRIBUTOS:

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    private final EnsambladorDTODescuento ensambladorDTODescuento;

    private final EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;

    //CONSTRUCTOR:

    public EnsambladorDTOProducto(
            EnsambladorDTOImpuesto ensambladorDTOImpuesto, EnsambladorDTODescuento ensambladorDTODescuento,
            EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento
    ) {
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
        this.ensambladorDTOPoliticaVencimiento = ensambladorDTOPoliticaVencimiento;
    }

    //VALIDACIONES:

    private void validarProducto(Producto producto){
        if (producto == null){
            throw new IllegalArgumentException("NO puedes ensamblar un DTO de un Producto Vacío.");
        }
    }

    //MÉTODOS:

    private DatosTotalesProductoDTO ensamblarDatosTotalesProducto(Producto producto, LocalDate fecha){
        validarProducto(producto);
        ImpuestoDTO datosImpuesto = this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(producto.getImpuesto());
        DescuentoDTO datosDescuento = this.ensambladorDTODescuento.ensamblarDatosDescuento(producto.getDescuento());
        if (producto instanceof ProductoRopa ropa){
            return new DatosTotalesProductoRopaDTO(
                    ropa.getCodigo(), ropa.getNombre(), ropa.getValorCompra(), ropa.getPorcentajeGanancia(),
                    ropa.getValorVenta(fecha), ropa.getStock(), datosImpuesto, datosDescuento, ropa.getTalla(),
                    ropa.isActivo()
            );
        } else if (producto instanceof ProductoPerecedero perecedero){
            PoliticaVencimientoDTO datosPoliticaVencimiento =
                    this.ensambladorDTOPoliticaVencimiento.ensamblarDatosPoliticaVencimiento(
                            perecedero.getPoliticaVencimiento()
                    );
            return new DatosTotalesProductoPerecederoDTO(
                    perecedero.getCodigo(), perecedero.getNombre(), perecedero.getValorCompra(),
                    perecedero.getPorcentajeGanancia(), perecedero.getValorVenta(fecha), perecedero.getStock(),
                    datosImpuesto, datosDescuento, perecedero.getFechaVencimiento(), datosPoliticaVencimiento,
                    perecedero.estaVencido(fecha), perecedero.isActivo()
            );
        } else {
            throw new IllegalStateException("Tipo de Producto no soportado por el Sistema");
        }
    }

    public DatosTotalesProductoRopaDTO ensamblarDatosProductoRopa(Producto producto, LocalDate fecha) {
        validarProducto(producto);
        return (DatosTotalesProductoRopaDTO) ensamblarDatosTotalesProducto(producto, fecha);
    }

    public List<DatosTotalesProductoRopaDTO> ensamblarDetalleProductosRopa(
            List<Producto> productosRopa, LocalDate fecha
    ) {
        List<DatosTotalesProductoRopaDTO> datosProductosRopa = new ArrayList<>();
        for (Producto producto:productosRopa){
            DatosTotalesProductoRopaDTO productoResumen =
                    (DatosTotalesProductoRopaDTO) ensamblarDatosTotalesProducto(producto, fecha);
            datosProductosRopa.add(productoResumen);
        }
        return datosProductosRopa;
    }

    public DatosTotalesProductoPerecederoDTO ensamblarDatosProductoPerecedero(
            ProductoPerecedero perecedero, LocalDate fecha
    ) {
        validarProducto(perecedero);
        return (DatosTotalesProductoPerecederoDTO) ensamblarDatosTotalesProducto(perecedero, fecha);
    }

    public List<DatosTotalesProductoPerecederoDTO> ensamblarDetalleProductosPerecedero(List<Producto> productosRopa){
        LocalDate fecha = LocalDate.now();
        List<DatosTotalesProductoPerecederoDTO> datosProductosRopa = new ArrayList<>();
        for (Producto producto:productosRopa){
            DatosTotalesProductoPerecederoDTO productoResumen =
                    (DatosTotalesProductoPerecederoDTO) ensamblarDatosTotalesProducto(producto, fecha);
            datosProductosRopa.add(productoResumen);
        }
        return datosProductosRopa;
    }

    public ProductoResumenDTO ensamblarProductoResumen(Producto producto, LocalDate fecha){
        validarProducto(producto);
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

