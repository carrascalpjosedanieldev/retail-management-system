package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.ReporteRecaudoDTO;
import RetailManagementSystem.aplicacion.dto.ventas.ResumenVentaDiaDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOFactura;
import RetailManagementSystem.aplicacion.servicios.ServicioFacturas;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.time.LocalDate;

public class OrquestadorHistoricoDeVentas {

    //ATRIBUTOS:

    private final ServicioFacturas servicioFacturas;

    private final EnsambladorDTOFactura ensambladorDTOFactura;

    //CONSTRUCTOR:

    public OrquestadorHistoricoDeVentas(ServicioFacturas servicioFacturas, EnsambladorDTOFactura ensambladorDTOFactura) {
        this.servicioFacturas = servicioFacturas;
        this.ensambladorDTOFactura = ensambladorDTOFactura;
    }

    //MÉTODOS:

    public ResumenVentaDiaDTO obtenerResumenHoy(){
        return ensambladorDTOFactura.ensamblarResumenVentaDia(
                this.servicioFacturas.obtenerResumenHoy()
        );
    }

    public ReporteRecaudoDTO obtenerReporteRecaudoEntre(UsuarioDTOCompleto usuario, LocalDate fechaInicio, LocalDate fechaFin){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.VER_HISTORIAL_VENTAS);
        return this.ensambladorDTOFactura.ensamblarReporteRecaudo(
                servicioFacturas.obtenerReporteRecaudo(fechaInicio, fechaFin)
        );
    }


}//===================================================================================================================//

