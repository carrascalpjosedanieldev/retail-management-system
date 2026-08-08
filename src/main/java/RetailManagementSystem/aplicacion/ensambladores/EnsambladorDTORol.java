package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTORol {

    //ATRIBUTOS:

    private final EnsambladorDTOPermiso ensambladorDTOPermiso;

    //CONSTRUCTOR:

    public EnsambladorDTORol(EnsambladorDTOPermiso ensambladorDTOPermiso) {
        this.ensambladorDTOPermiso = ensambladorDTOPermiso;
    }

    //MÉTODOS:

    public RolDTO ensamblarDatosRol(Rol rol){
        if (rol == null){
            throw new IllegalArgumentException("NO puedes ensamblar un DTO con un Rol Vacío.");
        }
        List<PermisoDTO> datosPermisos = new ArrayList<>();
        datosPermisos = this.ensambladorDTOPermiso.ensamblarDetallePermisos(rol.getPermisos().stream().toList());
        return new RolDTO(rol.getIdRol(), rol.getNombre(), datosPermisos, rol.isActivo());
    }

    public List<RolDTO> ensamblarDetalleRoles(List<Rol> roles){
        List<RolDTO> detalleRoles = new ArrayList<>();
        for (Rol rol:roles){
            RolDTO datosRol = this.ensamblarDatosRol(rol);
            detalleRoles.add(datosRol);
        }
        return detalleRoles;
    }

}//===================================================================================================================//

