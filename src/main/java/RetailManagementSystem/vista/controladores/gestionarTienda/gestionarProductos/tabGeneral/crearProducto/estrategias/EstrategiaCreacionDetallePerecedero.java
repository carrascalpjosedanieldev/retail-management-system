package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral.crearProducto.estrategias;

import RetailManagementSystem.aplicacion.dto.creacion.DetallePerecederoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral
        .crearProducto.FormularioPerecederoControlador;

import java.time.LocalDate;

public class EstrategiaCreacionDetallePerecedero
        implements EstrategiaCreacionDetalleProducto<DetallePerecederoDTO, FormularioPerecederoControlador>{

    @Override
    public DetallePerecederoDTO crearDetalle(FormularioPerecederoControlador controlador) {
        LocalDate fechaVencimiento = controlador.getFechaSeleccionada();
        PoliticaVencimientoDTO politicaV = controlador.getPoliticaSeleccionada();
        if (fechaVencimiento == null || politicaV == null){
            throw new IllegalStateException(
                    "Campos NO Seleccionados, Selecciona la Fecha de Vencimiento y la Política de Vencimiento"
            );
        }
        return new DetallePerecederoDTO(fechaVencimiento, politicaV.idPoliticaVencimiento());
    }

}//===================================================================================================================//

