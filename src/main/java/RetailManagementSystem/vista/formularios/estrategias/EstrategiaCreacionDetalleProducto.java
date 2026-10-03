package RetailManagementSystem.vista.formularios.estrategias;

import RetailManagementSystem.aplicacion.dto.consultas.DetalleCreacionProductoDTO;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.FormularioEspecificoControlador;

public interface EstrategiaCreacionDetalleProducto<T extends DetalleCreacionProductoDTO> {

    T crearDetalle(FormularioEspecificoControlador controlador);

}//===================================================================================================================//

