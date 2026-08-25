package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.servicios.ServicioDescuentos;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.math.BigDecimal;
import java.util.List;

public class OrquestadorDescuentos {

    //ATRIBUTOS:

    private final ServicioDescuentos servicioDescuentos;

    private final EnsambladorDTODescuento ensambladorDTODescuento;

    //CONSTRUCTOR:

    public OrquestadorDescuentos(ServicioDescuentos servicioDescuentos, EnsambladorDTODescuento ensambladorDTODescuento) {
        this.servicioDescuentos = servicioDescuentos;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
    }

    //MÉTODOS:

    public List<DescuentoDTO> obtenerTodosLosDescuentos() {
        return this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerTodosLosDescuentos()
        );
    }

    public List<DescuentoDTO> obtenerDescuentosActivos(){
        return this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerDescuentosActivos()
        );
    }

    public DescuentoDTO registrarDescuento(
            UsuarioDTOCompleto usuario, String nombre, BigDecimal porcentaje, boolean activo
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_DESCUENTOS);
        Descuento descuento = this.servicioDescuentos.registrarDescuento(nombre, porcentaje, activo);
        return this.ensambladorDTODescuento.ensamblarDatosDescuento(descuento);
    }

    public DescuentoDTO actualizarDescuento(
            UsuarioDTOCompleto usuario, int idDescuento, String nombre, BigDecimal porcentaje
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_DESCUENTOS);
        Descuento descuento = this.servicioDescuentos.actualizarDescuento(idDescuento, nombre, porcentaje);
        return this.ensambladorDTODescuento.ensamblarDatosDescuento(descuento);
    }

    public void cambiarEstadoDescuento(UsuarioDTOCompleto usuario, int idDescuento) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_DESCUENTOS);
        this.servicioDescuentos.cambiarEstadoDescuento(idDescuento);
    }

}//===================================================================================================================//

