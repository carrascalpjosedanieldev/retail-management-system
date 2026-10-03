package RetailManagementSystem.vista.formularios.estrategias;

import RetailManagementSystem.aplicacion.dto.consultas.DetallePerecederoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral
        .crearProducto.FormularioEspecificoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarProductos.tabGeneral
        .crearProducto.FormularioPerecederoControlador;

import java.time.LocalDate;

public class EstrategiaCreacionDetallePerecedero implements EstrategiaCreacionDetalleProducto<DetallePerecederoDTO>{

    @Override
    public DetallePerecederoDTO crearDetalle(FormularioEspecificoControlador controlador) {
        FormularioPerecederoControlador perecederoControlador = (FormularioPerecederoControlador) controlador;
        LocalDate fechaVencimiento = perecederoControlador.getFechaSeleccionada();
        PoliticaVencimientoDTO politicaV = perecederoControlador.getPoliticaSeleccionada();
        if (fechaVencimiento == null || politicaV == null){
            throw new IllegalStateException(
                    "Campos NO Seleccionados, Selecciona la Fecha de Vencimiento y la Política de Vencimiento"
            );
        }
        return new DetallePerecederoDTO(fechaVencimiento, politicaV.idPoliticaVencimiento());
    }

}//===================================================================================================================//

