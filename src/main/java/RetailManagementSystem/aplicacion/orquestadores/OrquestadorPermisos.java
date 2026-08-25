package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOPermiso;
import RetailManagementSystem.aplicacion.servicios.ServicioPermiso;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.util.ArrayList;
import java.util.List;

public class OrquestadorPermisos {

    //ATRIBUTOS:

    private final EnsambladorDTOPermiso ensambladorDTOPermiso;

    private final ServicioPermiso servicioPermiso;

    //CONSTRUCTOR:

    public OrquestadorPermisos(EnsambladorDTOPermiso ensambladorDTOPermiso, ServicioPermiso servicioPermiso) {
        this.ensambladorDTOPermiso = ensambladorDTOPermiso;
        this.servicioPermiso = servicioPermiso;
    }

    //MÉTODOS:

    public List<PermisoDTO> obtenerTodosLosPermisos(){
        List<PermisoDTO> todosLosPermisos = new ArrayList<>();
        todosLosPermisos.addAll(
                this.ensambladorDTOPermiso.ensamblarDetallePermisos(
                        this.servicioPermiso.obtenerPermisosActivos()
                )
        );
        todosLosPermisos.addAll(
                this.ensambladorDTOPermiso.ensamblarDetallePermisos(
                        this.servicioPermiso.obtenerPermisosInactivos()
                )
        );
        return todosLosPermisos;
    }

    public List<PermisoDTO> obtenerPermisosActivos(){
        return this.ensambladorDTOPermiso.ensamblarDetallePermisos(
                this.servicioPermiso.obtenerPermisosActivos()
        );
    }

    public void cambiarEstadoPermiso(UsuarioDTOCompleto usuario, int idPermiso, boolean activoActual){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.GESTIONAR_PERMISOS);
        this.servicioPermiso.cambiarEstadoPermiso(idPermiso, activoActual);
    }

}//===================================================================================================================//

