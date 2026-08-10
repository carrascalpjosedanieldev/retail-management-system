package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTORol;
import RetailManagementSystem.aplicacion.servicios.ServicioRol;

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

}//===================================================================================================================//

