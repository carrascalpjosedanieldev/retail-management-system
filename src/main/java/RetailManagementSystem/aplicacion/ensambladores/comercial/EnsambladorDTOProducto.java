package RetailManagementSystem.aplicacion.ensambladores.comercial;

import RetailManagementSystem.aplicacion.dto.comercial.*;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOImpuesto;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOPoliticaVencimiento;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOProducto {

    //ATRIBUTOS:

    private final CalculadoraPrecios calculadoraPrecios;

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    private final EnsambladorDTODescuento ensambladorDTODescuento;

    private final EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;

    //CONSTRUCTOR:

    public EnsambladorDTOProducto(
            CalculadoraPrecios calculadoraPrecios, EnsambladorDTOImpuesto ensambladorDTOImpuesto,
            EnsambladorDTODescuento ensambladorDTODescuento,
            EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento
    ) {
        this.calculadoraPrecios = calculadoraPrecios;
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
        this.ensambladorDTOPoliticaVencimiento = ensambladorDTOPoliticaVencimiento;
    }

    //MÉTODOS:

    public DatosTotalesProductoDTO ensamblarDatosTotalesProducto(
            Producto producto, ContextoEvaluacion contextoEvaluacion
    ) {
        ImpuestoDTO datosImpuesto = this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(producto.getImpuesto());
        DescuentoDTO datosDescuento = this.ensambladorDTODescuento.ensamblarDatosDescuento(producto.getDescuento());
        BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(producto, contextoEvaluacion);
        if (producto instanceof ProductoRopa ropa){
            return new DatosTotalesProductoRopaDTO(
                    ropa.getCodigo(), ropa.getNombre(), ropa.getValorCompra(), ropa.getPorcentajeGanancia(),
                    valorVenta, ropa.getStock(), datosImpuesto, datosDescuento, ropa.getTalla(),
                    ropa.isActivo()
            );
        } else if (producto instanceof ProductoPerecedero perecedero){
            PoliticaVencimientoDTO datosPoliticaVencimiento =
                    this.ensambladorDTOPoliticaVencimiento.ensamblarDatosPoliticaVencimiento(
                            perecedero.getPoliticaVencimiento()
                    );
            if (contextoEvaluacion.getFechaEvaluacion().isEmpty()){
                throw new IllegalStateException("Se Requiere una Fecha para Calcular el Valor del Producto");
            }
            return new DatosTotalesProductoPerecederoDTO(
                    perecedero.getCodigo(), perecedero.getNombre(), perecedero.getValorCompra(),
                    perecedero.getPorcentajeGanancia(), valorVenta, perecedero.getStock(), datosImpuesto,
                    datosDescuento, perecedero.getFechaVencimiento(), datosPoliticaVencimiento,
                    perecedero.estaVencido(contextoEvaluacion.getFechaEvaluacion().get()), perecedero.isActivo()
            );
        } else {
            throw new IllegalStateException("Tipo de Producto no soportado por el Sistema");
        }
    }

    public List<DatosTotalesProductoRopaDTO> ensamblarDetalleProductosRopa(
            List<Producto> productosRopa, ContextoEvaluacion contextoEvaluacion
    ) {
        List<DatosTotalesProductoRopaDTO> datosProductosRopa = new ArrayList<>();
        for (Producto producto:productosRopa){
            DatosTotalesProductoRopaDTO productoResumen =
                    (DatosTotalesProductoRopaDTO) ensamblarDatosTotalesProducto(producto, contextoEvaluacion);
            datosProductosRopa.add(productoResumen);
        }
        return datosProductosRopa;
    }

    public List<DatosTotalesProductoPerecederoDTO> ensamblarDetalleProductosPerecedero(
            List<Producto> productosPerecederos, ContextoEvaluacion contextoEvaluacion
    ) {
        List<DatosTotalesProductoPerecederoDTO> datosProductosRopa = new ArrayList<>();
        for (Producto producto: productosPerecederos){
            DatosTotalesProductoPerecederoDTO productoResumen =
                    (DatosTotalesProductoPerecederoDTO) ensamblarDatosTotalesProducto(producto, contextoEvaluacion);
            datosProductosRopa.add(productoResumen);
        }
        return datosProductosRopa;
    }

    public ProductoResumenDTO ensamblarProductoResumen(Producto producto, ContextoEvaluacion contextoEvaluacion){
        BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(producto, contextoEvaluacion);
        return new ProductoResumenDTO(
                producto.getCodigo(), producto.getNombre(), valorVenta, producto.getStock(), producto.isActivo()
        );
    }

    public List<ProductoResumenDTO> ensamblarDetalleProductosResumen(
            List<Producto> productos, ContextoEvaluacion contextoEvaluacion
    ) {
        List<ProductoResumenDTO> resumenProductos = new ArrayList<>();
        for (Producto producto:productos){
            ProductoResumenDTO productoResumen = ensamblarProductoResumen(producto, contextoEvaluacion);
            resumenProductos.add(productoResumen);
        }
        return resumenProductos;
    }

}//===================================================================================================================//

