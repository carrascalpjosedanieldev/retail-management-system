package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOInventario;
import RetailManagementSystem.aplicacion.servicios.ServicioInventario;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.util.List;

public class OrquestadorInventarios {

    //ATRIBUTOS:

    private final ServicioInventario servicioInventario;

    private final EnsambladorDTOInventario ensambladorDTOInventario;

    //CONSTRUCTOR:

    public OrquestadorInventarios(ServicioInventario servicioInventario, EnsambladorDTOInventario ensambladorDTOInventario) {
        this.servicioInventario = servicioInventario;
        this.ensambladorDTOInventario = ensambladorDTOInventario;
    }

    //MÉTODOS:

    public InventarioDTO actualizarInventario(
            UsuarioDTOCompleto usuario, int idInventario, String nombreNuevo
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.EDITAR_INVENTARIOS);
        return this.ensambladorDTOInventario.ensamblarDatosInventario(
                this.servicioInventario.actualizarInventario(idInventario, nombreNuevo)
        );
    }

    public InventarioDTO registrarInventario(
            UsuarioDTOCompleto usuario, String nombre, int capacidadMaxima
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.REGISTRAR_INVENTARIOS);
        return this.ensambladorDTOInventario.ensamblarDatosInventario(
                this.servicioInventario.registrarInventario(nombre, capacidadMaxima)
        );
    }

    public List<InventarioDTO> obtenerTodosLosInventarios(){
        return this.ensambladorDTOInventario.ensamblarDetalleInventarioGeneral(
                this.servicioInventario.obtenerTodosLosInventarios()
        );
    }

}//===================================================================================================================//

