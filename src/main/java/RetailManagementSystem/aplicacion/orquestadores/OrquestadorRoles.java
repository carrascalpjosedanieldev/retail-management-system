package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTORol;
import RetailManagementSystem.aplicacion.servicios.ServicioRol;
import RetailManagementSystem.dominio.entidades.seguridad.Permiso;

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

    public RolDTO registrarRolNuevo(String nombreRol, boolean activo, List<PermisoDTO> permisos) {
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
        return this.ensambladorDTORol.ensamblarDatosRol(
                this.servicioRol.registrarRol(nombreRol.toUpperCase(), activo, permisosParaELRol)
        );
    }

    public RolDTO actualizarRol(int idRol, String nombreNuevo, boolean activo){
        return this.ensambladorDTORol.ensamblarDatosRol(
                this.servicioRol.actualzarRol(idRol, nombreNuevo, activo)
        );
    }

}//===================================================================================================================//

