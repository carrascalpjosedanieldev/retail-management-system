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
        String tallaSt = ropaControlador.getTallaSeleccionada();
        if (tallaSt == null){
            throw new IllegalStateException("Talla NO Seleccionada, Debes Seleccionar una Talla");
        }
        try {

            Talla talla = Talla.valueOf(tallaSt);
            return new DetalleRopaDTO(talla);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("");
        }
    }

}//===================================================================================================================//

