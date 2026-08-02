package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.UsuarioDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOUsuario;
import RetailManagementSystem.aplicacion.servicios.ServicioUsuario;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;

import java.time.LocalDateTime;
import java.util.Arrays;

public class OrquestadorLogin {

    //ATRIBUTOS:

    private final ServicioUsuario servicioUsuario;

    private final EnsambladorDTOUsuario ensambladorDTOUsuario;

    //CONSTRUCTOR:

    public OrquestadorLogin(ServicioUsuario servicioUsuario, EnsambladorDTOUsuario ensambladorDTOUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.ensambladorDTOUsuario = ensambladorDTOUsuario;
    }

    //MÉTODOS:

    public UsuarioDTO autenticar(String email, char[] contrasenaPlana, LocalDateTime fechaReferencia){
        try {
            Usuario usuario = this.servicioUsuario.validarYObtenerUsuarioValido(email, contrasenaPlana, fechaReferencia);
            return this.ensambladorDTOUsuario.ensamblarDTOUsuario(usuario);
        } finally {
            Arrays.fill(contrasenaPlana, '\0');
        }
    }

}

