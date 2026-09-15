package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOUsuario;
import RetailManagementSystem.aplicacion.servicios.ServicioLogin;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.time.LocalDateTime;
import java.util.Arrays;

public class OrquestadorLogin {

    //ATRIBUTOS:

    private final ServicioLogin servicioLogin;

    private final EnsambladorDTOUsuario ensambladorDTOUsuario;

    //CONSTRUCTOR:

    public OrquestadorLogin(ServicioLogin servicioLogin, EnsambladorDTOUsuario ensambladorDTOUsuario) {
        this.servicioLogin = servicioLogin;
        this.ensambladorDTOUsuario = ensambladorDTOUsuario;
    }

    //MÉTODOS:

    public UsuarioDTOCompleto autenticar(String email, char[] contrasenaPlana){
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El Correo Electrónico NO puede estar Vacío.");
        }
        if (contrasenaPlana == null || contrasenaPlana.length == 0) {
            throw new IllegalArgumentException("La Contraseña NO puede estar Vacía.");
        }
        try {
            return this.ensambladorDTOUsuario.ensamblarDTOUsuarioCompleto(
                    this.servicioLogin.validarIngresoYObtenerUsuarioValido(
                            email.trim(), contrasenaPlana, LocalDateTime.now()
                    )
            );
        } finally {
            Arrays.fill(contrasenaPlana, '\0');
        }
    }

    public void cambiarContrasenaDefinitiva(Long idUsuario, char[] nuevaContrasenaPlana){
        this.servicioLogin.cambiarContrasenaDefinitiva(idUsuario, nuevaContrasenaPlana);
    }

    public char[] restablecerContrasenaPorAdmin(UsuarioDTOCompleto usuario, Long idUsuario){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.RESTABLECER_CONTRASENA_USUARIO);
        return this.servicioLogin.restablecerContrasenaPorAdmin(idUsuario);
    }

}//===================================================================================================================//

