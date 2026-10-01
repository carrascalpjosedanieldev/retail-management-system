package RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoDTO;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import java.util.List;

public interface EstrategiaEnsambladoDTOProducto<T extends DatosTotalesProductoDTO> {

    T ensamblarDatosTotalesProducto(Producto producto, ContextoEvaluacion contextoEvaluacion);

    List<T> ensamblarDetalleProductos(
            List<Producto> listaProductos, ContextoEvaluacion contextoEvaluacion
    );

}//===================================================================================================================//

