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

    private List<PermisoDTO> ensamblarDetallePermisos(List<Permiso> permisos, List<PermisoDTO> detalle){
        for (Permiso permiso:permisos){
            PermisoDTO datosPermiso = this.ensamblarDatosPermiso(permiso);
            detalle.add(datosPermiso);
        }
        return detalle;
    }

    public List<PermisoDTO> ensamblarDetallePermisosActivos(List<Permiso> permisos){
        List<PermisoDTO> detallePermisosActivos = new ArrayList<>();
        return this.ensamblarDetallePermisos(permisos, detallePermisosActivos);
    }

    public List<PermisoDTO> ensamblarDetallePermisosInactivos(List<Permiso> permisos){
        List<PermisoDTO> detallePermisosInactivos = new ArrayList<>();
        return this.ensamblarDetallePermisos(permisos, detallePermisosInactivos);
    }



}//===================================================================================================================//

