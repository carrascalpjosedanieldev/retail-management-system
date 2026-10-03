package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.estrategias;

import RetailManagementSystem.aplicacion.dto.creacion.DetalleCreacionProductoDTO;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.FormularioEspecificoControlador;

public interface EstrategiaCreacionDetalleProducto
        <T extends DetalleCreacionProductoDTO, C extends FormularioEspecificoControlador> {

    T crearDetalle(C controlador);

}//===================================================================================================================//

