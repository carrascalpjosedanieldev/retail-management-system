package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.ensambladores.comercial.EnsambladorDTOServicio;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioServicios;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrquestadorServicios {

    //ATRIBUTOS:

    private final ServicioServicios servicioServicios;

    private final EnsambladorDTOServicio ensambladorDTOServicio;

    //CONSTRUCTOR:

    public OrquestadorServicios(
            ServicioServicios servicioServicios, EnsambladorDTOServicio ensambladorDTOServicio
    ) {
        this.servicioServicios = servicioServicios;
        this.ensambladorDTOServicio = ensambladorDTOServicio;
    }

    //MÉTODOS:

    public List<ServicioDTO> obtenerTodosLosServicios(LocalDate fecha){
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOServicio.ensamblarDatosCatalogoServicios(
                this.servicioServicios.obtenerTodosLosServicios(), contextoEvaluacion
        );
    }

    public ServicioDTO registrarServicio(
            UsuarioDTOCompleto usuario, String nombre, BigDecimal precioBase, int idImpuesto, int idDescuento,
            LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.REGISTRAR_SERVICIOS);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOServicio.ensamblarServicio(
                this.servicioServicios.registrarServicioNuevo(
                        nombre, precioBase, idImpuesto, idDescuento
                ), contextoEvaluacion
        );
    }

    public ServicioDTO actualizarServicio(
            UsuarioDTOCompleto usuario, String codigoServicio, String nuevoNombre, BigDecimal nuevoPrecioBase,
            int idImpuesto, int idDescuento, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.MODIFICAR_SERVICIOS);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOServicio.ensamblarServicio(
                this.servicioServicios.actualizarServicio(
                        codigoServicio, nuevoNombre, nuevoPrecioBase, idImpuesto, idDescuento
                ), contextoEvaluacion
        );
    }

    public void cambiarEstadoServicio(UsuarioDTOCompleto usuario, String codigoServicio){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.CAMBIAR_ESTADO_SERVICIOS);
        this.servicioServicios.cambiarEstadoServicio(codigoServicio);
    }

}//===================================================================================================================//

