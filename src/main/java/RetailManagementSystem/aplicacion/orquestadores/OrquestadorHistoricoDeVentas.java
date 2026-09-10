package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.consultas.ReporteRecaudoDTO;
import RetailManagementSystem.aplicacion.dto.consultas.ResumenVentaDiaDTO;
import RetailManagementSystem.aplicacion.servicios.ServicioFacturas;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.time.LocalDate;

public class OrquestadorHistoricoDeVentas {

    //ATRIBUTOS:

    private final ServicioFacturas servicioFacturas;

    //CONSTRUCTOR:

    public OrquestadorHistoricoDeVentas(ServicioFacturas servicioFacturas) {
        this.servicioFacturas = servicioFacturas;
    }

    //MÉTODOS:

    public ResumenVentaDiaDTO obtenerResumenHoy(){
        return this.servicioFacturas.obtenerResumenHoy();
    }

    public ReporteRecaudoDTO obtenerReporteRecaudoEntre(UsuarioDTOCompleto usuario, LocalDate fechaInicio, LocalDate fechaFin){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.VER_HISTORIAL_VENTAS);
        return servicioFacturas.obtenerReporteRecaudo(fechaInicio, fechaFin);
    }

}//===================================================================================================================//

