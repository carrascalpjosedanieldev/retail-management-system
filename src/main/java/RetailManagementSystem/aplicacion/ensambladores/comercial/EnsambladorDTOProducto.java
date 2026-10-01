package RetailManagementSystem.aplicacion.ensambladores.comercial;

import RetailManagementSystem.aplicacion.dto.comercial.*;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias.EstrategiaEnsambladoDTOProducto;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnsambladorDTOProducto {

    //ATRIBUTOS:

    private final Map<TipoProducto, EstrategiaEnsambladoDTOProducto<?>> estrategiasEnsamblado;

    private final CalculadoraPrecios calculadoraPrecios;

    //CONSTRUCTOR:

    public EnsambladorDTOProducto(
            Map<TipoProducto, EstrategiaEnsambladoDTOProducto<?>> estrategiasEnsamblado,
            CalculadoraPrecios calculadoraPrecios
    ) {
        this.estrategiasEnsamblado = estrategiasEnsamblado;
        this.calculadoraPrecios = calculadoraPrecios;
    }

    //MÉTODOS:

    @SuppressWarnings("unchecked")
    public <T extends DatosTotalesProductoDTO> T ensamblarDatosTotalesProducto(
            Producto producto, ContextoEvaluacion contextoEvaluacion
    ) {
        EstrategiaEnsambladoDTOProducto<T> estrategia =
                (EstrategiaEnsambladoDTOProducto<T>) this.estrategiasEnsamblado.get(producto.getTipoProducto());
        if (estrategia == null) {
            throw new IllegalStateException(
                    "No existe una estrategia de ensamblado registrada para: " + producto.getTipoProducto()
            );
        }
        return estrategia.ensamblarDatosTotalesProducto(producto, contextoEvaluacion);
    }

    public <T extends Producto, R extends DatosTotalesProductoDTO> List<R> ensamblarDetalleProductos(
            List<T> listaProductos, ContextoEvaluacion contextoEvaluacion
    ) {
        if (listaProductos.isEmpty()){
            return new ArrayList<>();
        }
        List<R> datosProductos = new ArrayList<>();
        for (T producto:listaProductos){
            R productoResumen = ensamblarDatosTotalesProducto(producto, contextoEvaluacion);
            datosProductos.add(productoResumen);
        }
        return datosProductos;
    }

    public ProductoResumenDTO ensamblarProductoResumen(
            Producto producto, ContextoEvaluacion contextoEvaluacion
    ) {
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

