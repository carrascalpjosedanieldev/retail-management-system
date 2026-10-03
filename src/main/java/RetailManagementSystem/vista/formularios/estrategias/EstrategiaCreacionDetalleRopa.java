package RetailManagementSystem.vista.formularios.estrategias;

import RetailManagementSystem.aplicacion.dto.consultas.DetalleRopaDTO;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.FormularioEspecificoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral
        .crearProducto.FormularioRopaControlador;

public class EstrategiaCreacionDetalleRopa implements EstrategiaCreacionDetalleProducto<DetalleRopaDTO> {

    @Override
    public DetalleRopaDTO crearDetalle(FormularioEspecificoControlador controlador) {
        FormularioRopaControlador ropaControlador = (FormularioRopaControlador) controlador;
        Talla talla = ropaControlador.getTallaSeleccionada();
        if (talla == null){
            throw new IllegalStateException("Talla NO Seleccionada, Debes Seleccionar una Talla");
        }
        return new DetalleRopaDTO(talla);
    }

}//===================================================================================================================//

