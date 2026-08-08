package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.dominio.entidades.seguridad.Permiso;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOPermiso {

    //CONSTRUCTOR:

    public EnsambladorDTOPermiso() {
    }

    //MÉTODOS:

    public PermisoDTO ensamblarDatosPermiso(Permiso permiso) {
        if (permiso == null){
            throw new IllegalArgumentException("NO puedes ensamblar un DTO con un Permiso Vacío.");
        }
        return new PermisoDTO(
                permiso.getIdPermiso(), permiso.getNombre(), permiso.getDescripcion(), permiso.getModulo(),
                permiso.isActivo()
        );
    }

    public List<PermisoDTO> ensamblarDetallePermisos(List<Permiso> permisos){
        List<PermisoDTO> detallePermisos = new ArrayList<>();
        for (Permiso permiso:permisos){
            PermisoDTO datosPermiso = this.ensamblarDatosPermiso(permiso);
            detallePermisos.add(datosPermiso);
        }
        return detallePermisos;
    }

}//===================================================================================================================//

