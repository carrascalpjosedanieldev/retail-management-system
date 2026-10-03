package RetailManagementSystem.aplicacion.fabricas;

import RetailManagementSystem.aplicacion.dto.creacion.DatosGeneralesCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.DetalleRopaDTO;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

public class EstrategiaFabricarRopa implements
        EstrategiaFabricarProducto<ProductoRopa, DetalleRopaDTO> {

    @Override
    public ProductoRopa fabricarProducto(
            DatosGeneralesCreacionProductoDTO general, DetalleRopaDTO detalle, ContextoEvaluacion contextoEvaluacion,
            Impuesto impuesto, Descuento descuento
    ) {
        return ProductoRopa.crearNuevo(
                general.nombre(), general.valorCompra(), general.ganancia(), general.stock(),
                impuesto, descuento, detalle.talla()
        );
    }

}//===================================================================================================================//

