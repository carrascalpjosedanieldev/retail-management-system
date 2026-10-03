package RetailManagementSystem.aplicacion.fabricas;

import RetailManagementSystem.aplicacion.dto.creacion.DatosGeneralesCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.DetalleCreacionProductoDTO;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

public interface EstrategiaFabricarProducto<T extends Producto, D extends DetalleCreacionProductoDTO> {

    T fabricarProducto(
            DatosGeneralesCreacionProductoDTO general, D detalle, ContextoEvaluacion contextoEvaluacion,
            Impuesto impuesto, Descuento descuento
    );

}//===================================================================================================================//

