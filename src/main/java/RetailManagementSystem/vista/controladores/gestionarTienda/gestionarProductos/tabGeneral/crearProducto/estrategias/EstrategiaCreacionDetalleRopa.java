package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.estrategias;

import RetailManagementSystem.aplicacion.dto.creacion.DetalleRopaDTO;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral
        .crearProducto.FormularioRopaControlador;

public class EstrategiaCreacionDetalleRopa implements
        EstrategiaCreacionDetalleProducto<DetalleRopaDTO, FormularioRopaControlador> {

    @Override
    public DetalleRopaDTO crearDetalle(FormularioRopaControlador controlador) {
        Talla talla = controlador.getTallaSeleccionada();
        if (talla == null){
            throw new IllegalStateException("Talla NO Seleccionada, Debes Seleccionar una Talla");
        }
        return new DetalleRopaDTO(talla);
    }

}//===================================================================================================================//

