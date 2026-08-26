package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTORol;
import RetailManagementSystem.aplicacion.servicios.ServicioRol;
import RetailManagementSystem.dominio.entidades.seguridad.Permiso;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.util.ArrayList;
import java.util.List;

public class OrquestadorRoles {

    //ATRIBUTOS:

    private final ServicioRol servicioRol;

    private final EnsambladorDTORol ensambladorDTORol;

    //CONSTRUCTOR:

    public OrquestadorRoles(ServicioRol servicioRol, EnsambladorDTORol ensambladorDTORol) {
        this.servicioRol = servicioRol;
        this.ensambladorDTORol = ensambladorDTORol;
    }

    //MÉTODOS:

    public List<RolDTO> obtenerTodosLosRoles(){
        return this.ensambladorDTORol.ensamblarDetalleRoles(
                this.servicioRol.obtenerRoles()
        );
    }

    public void registrarRolNuevo(
            UsuarioDTOCompleto usuario, String nombreRol, boolean activo, List<PermisoDTO> permisos
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.REGISTRAR_ROLES);
        List<Permiso> permisosParaELRol = new ArrayList<>();
        for (PermisoDTO permisoDTO:permisos){
            Permiso permiso = Permiso.reconstruirDesdeBD(
                    permisoDTO.idPermiso(),
                    permisoDTO.nombre(),
                    permisoDTO.descripcion(),
                    permisoDTO.modulo(),
                    permisoDTO.activo()
            );
            permisosParaELRol.add(permiso);
        }
        this.servicioRol.registrarRol(nombreRol.toUpperCase(), activo, permisosParaELRol);
    }

    public RolDTO actualizarDatosRol(
            UsuarioDTOCompleto usuario, int idRol, String nombreNuevo, boolean activo
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.EDITAR_ROLES);
        return this.ensambladorDTORol.ensamblarDatosRol(
                this.servicioRol.actualzarDatosRol(idRol, nombreNuevo, activo)
        );
    }

    public void actualizarPermisosRol(
            UsuarioDTOCompleto usuario, int idRol, List<PermisoDTO> listaPermisosActualizada
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_PERMISOS_DE_ROLES);
        List<Permiso> permisosParaELRol = new ArrayList<>();
        for (PermisoDTO permisoDTO:listaPermisosActualizada){
            Permiso permiso = Permiso.reconstruirDesdeBD(
                    permisoDTO.idPermiso(),
                    permisoDTO.nombre(),
                    permisoDTO.descripcion(),
                    permisoDTO.modulo(),
                    permisoDTO.activo()
            );
            permisosParaELRol.add(permiso);
        }
        this.servicioRol.actualizarPermisosRol(idRol, permisosParaELRol);
    }

}//===================================================================================================================//

