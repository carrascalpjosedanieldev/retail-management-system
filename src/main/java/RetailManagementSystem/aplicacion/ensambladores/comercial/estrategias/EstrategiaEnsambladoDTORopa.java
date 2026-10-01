package RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoRopaDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOImpuesto;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EstrategiaEnsambladoDTORopa implements EstrategiaEnsambladoDTOProducto<DatosTotalesProductoRopaDTO> {

    //ATRIBUTOS:

    private final CalculadoraPrecios calculadoraPrecios;

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    private final EnsambladorDTODescuento ensambladorDTODescuento;

    //CONSTRUCTOR:

    public EstrategiaEnsambladoDTORopa(
            CalculadoraPrecios calculadoraPrecios, EnsambladorDTOImpuesto ensambladorDTOImpuesto,
            EnsambladorDTODescuento ensambladorDTODescuento
    ) {
        this.calculadoraPrecios = calculadoraPrecios;
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
    }

    //MÉTODOS:

    @Override
    public DatosTotalesProductoRopaDTO ensamblarDatosTotalesProducto(
            Producto producto, ContextoEvaluacion contextoEvaluacion
    ) {
        ProductoRopa ropa = (ProductoRopa) producto;
        ImpuestoDTO datosImpuesto = this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(ropa.getImpuesto());
        DescuentoDTO datosDescuento = this.ensambladorDTODescuento.ensamblarDatosDescuento(ropa.getDescuento());
        BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(ropa, contextoEvaluacion);
        return new DatosTotalesProductoRopaDTO(
                ropa.getCodigo(), ropa.getNombre(), ropa.getValorCompra(), ropa.getPorcentajeGanancia(),
                valorVenta, ropa.getStock(), datosImpuesto, datosDescuento, ropa.getTalla(),
                ropa.isActivo()
        );
    }

    @Override
    public List<DatosTotalesProductoRopaDTO> ensamblarDetalleProductos(
            List<Producto> listaProductos, ContextoEvaluacion contextoEvaluacion
    ) {
        List<DatosTotalesProductoRopaDTO> datosProductosRopa = new ArrayList<>();
        for (Producto producto:listaProductos){
            DatosTotalesProductoRopaDTO productoResumen = ensamblarDatosTotalesProducto(producto, contextoEvaluacion);
            datosProductosRopa.add(productoResumen);
        }
        return datosProductosRopa;
    }

}//===================================================================================================================//

