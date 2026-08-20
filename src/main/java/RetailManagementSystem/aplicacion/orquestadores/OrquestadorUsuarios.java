package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOUsuario;
import RetailManagementSystem.aplicacion.servicios.ServicioUsuario;

import java.util.List;

public class OrquestadorUsuarios {

    //ATRIBUTOS:

    private final ServicioUsuario servicioUsuario;

    private final EnsambladorDTOUsuario ensambladorDTOUsuario;

    //CONSTRUCTOR:

    public OrquestadorUsuarios(ServicioUsuario servicioUsuario, EnsambladorDTOUsuario ensambladorDTOUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.ensambladorDTOUsuario = ensambladorDTOUsuario;
    }

    //MÉTODOS:

    public List<UsuarioDTO> obtenerTodosLosUsuarios(){
        return this.ensambladorDTOUsuario.ensamblarDetalleUsuarios(
                this.servicioUsuario.obtenerTodosLosUsuarios()
        );
    }

}//===================================================================================================================//

