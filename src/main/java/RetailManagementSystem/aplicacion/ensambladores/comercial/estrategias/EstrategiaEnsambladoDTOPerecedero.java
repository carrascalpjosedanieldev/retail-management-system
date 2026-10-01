package RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoPerecederoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOImpuesto;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOPoliticaVencimiento;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstrategiaEnsambladoDTOPerecedero implements EstrategiaEnsambladoDTOProducto<DatosTotalesProductoPerecederoDTO> {

    //ATRIBUTOS:

    private final CalculadoraPrecios calculadoraPrecios;

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    private final EnsambladorDTODescuento ensambladorDTODescuento;

    private final EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;

    //CONSTRUCTOR:

    public EstrategiaEnsambladoDTOPerecedero(
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

    @Override
    public DatosTotalesProductoPerecederoDTO ensamblarDatosTotalesProducto(
            Producto producto, ContextoEvaluacion contextoEvaluacion
    ) {
        ProductoPerecedero perecedero = (ProductoPerecedero) producto;
        ImpuestoDTO datosImpuesto = this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(perecedero.getImpuesto());
        DescuentoDTO datosDescuento = this.ensambladorDTODescuento.ensamblarDatosDescuento(perecedero.getDescuento());
        BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(perecedero, contextoEvaluacion);
        PoliticaVencimientoDTO datosPoliticaVencimiento =
                this.ensambladorDTOPoliticaVencimiento.ensamblarDatosPoliticaVencimiento(
                        perecedero.getPoliticaVencimiento()
                );
        if (contextoEvaluacion.getFechaEvaluacion().isEmpty()){
            throw new IllegalStateException("Se Requiere una Fecha para Calcular el Valor del Producto");
        }
        LocalDate fecha = contextoEvaluacion.getFechaEvaluacion().get();
        return new DatosTotalesProductoPerecederoDTO(
                perecedero.getCodigo(), perecedero.getNombre(), perecedero.getValorCompra(),
                perecedero.getPorcentajeGanancia(), valorVenta, perecedero.getStock(), datosImpuesto,
                datosDescuento, perecedero.getFechaVencimiento(), datosPoliticaVencimiento,
                perecedero.estaVencido(fecha), perecedero.isActivo()
        );
    }

    @Override
    public List<DatosTotalesProductoPerecederoDTO> ensamblarDetalleProductos(List<Producto> listaProductos, ContextoEvaluacion contextoEvaluacion) {
        List<DatosTotalesProductoPerecederoDTO> datosProductosPerecedero = new ArrayList<>();
        for (Producto producto: listaProductos){
            DatosTotalesProductoPerecederoDTO productoResumen =
                    ensamblarDatosTotalesProducto(producto, contextoEvaluacion);
            datosProductosPerecedero.add(productoResumen);
        }
        return datosProductosPerecedero;
    }

}//===================================================================================================================//

