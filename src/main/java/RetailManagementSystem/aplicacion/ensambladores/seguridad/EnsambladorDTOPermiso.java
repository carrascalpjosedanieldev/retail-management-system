package RetailManagementSystem.aplicacion.ensambladores.seguridad;

import RetailManagementSystem.aplicacion.dto.seguridad.PermisoDTO;
import RetailManagementSystem.dominio.entidades.seguridad.Permiso;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOPermiso {

    //CONSTRUCTOR:

    public EnsambladorDTOPermiso() { }

    //MÉTODOS:

    public PermisoDTO ensamblarDatosPermiso(Permiso permiso) {
        return new PermisoDTO(
                permiso.getIdPermiso(), permiso.getNombre(), permiso.getDescripcion(), permiso.getModulo(),
                permiso.isActivo()
        );
    }

    public List<PermisoDTO> ensamblarDetallePermisos(List<Permiso> permisos){
        List<PermisoDTO> detallePermisos = new ArrayList<>();
        for (Permiso permiso:permisos){
            PermisoDTO datosPermiso = ensamblarDatosPermiso(permiso);
            detallePermisos.add(datosPermiso);
        }
        return detallePermisos;
    }

}//===================================================================================================================//

