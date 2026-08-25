package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOImpuesto;
import RetailManagementSystem.aplicacion.servicios.ServicioImpuestos;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.math.BigDecimal;
import java.util.List;

public class OrquestadorImpuestos {

    //ATRIBUTOS:

    private final ServicioImpuestos servicioImpuestos;

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    //CONSTRUCTOR:

    public OrquestadorImpuestos(ServicioImpuestos servicioImpuestos, EnsambladorDTOImpuesto ensambladorDTOImpuesto) {
        this.servicioImpuestos = servicioImpuestos;
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
    }

    //MÉTODOS:

    public List<ImpuestoDTO> obtenerTodosLosImpuestos() {
        return this.ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                this.servicioImpuestos.obtenerTodosLosImpuestos()
        );
    }

    public List<ImpuestoDTO> obtenerImpuestosActivos(){
        return this.ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                this.servicioImpuestos.obtenerImpuestosActivos()
        );
    }

    public ImpuestoDTO registrarImpuesto(
            UsuarioDTOCompleto usuario, String nombre, BigDecimal porcentaje, boolean activo
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_IMPUESTOS);
        Impuesto impuesto = this.servicioImpuestos.registrarImpuesto(nombre, porcentaje, activo);
        return this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(impuesto);
    }

    public ImpuestoDTO actualizarImpuesto(
            UsuarioDTOCompleto usuario, int idImpuesto, String nombre, BigDecimal porcentaje
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_IMPUESTOS);
        Impuesto impuesto = this.servicioImpuestos.actualizarImpuesto(idImpuesto, nombre, porcentaje);
        return this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(impuesto);
    }

    public void cambiarEstadoImpuesto(UsuarioDTOCompleto usuario, int idImpuesto) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_IMPUESTOS);
        this.servicioImpuestos.cambiarEstadoImpuesto(idImpuesto);
    }

}//===================================================================================================================//

