package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.excepciones.CredencialesInvalidasException;
import RetailManagementSystem.dominio.excepciones.UsuarioBloqueadoException;
import RetailManagementSystem.dominio.excepciones.UsuarioInactivoException;
import RetailManagementSystem.dominio.puertos.RepositorioUsuario;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ServicioUsuario {

    //ATRIBUTOS:

    private final RepositorioUsuario repositorioUsuario;

    private final CodificadorContrasenas codificadorContrasenas;

    //CONSTRUCTOR:

    public ServicioUsuario(RepositorioUsuario repositorioUsuario, CodificadorContrasenas codificadorContrasenas) {
        this.repositorioUsuario = repositorioUsuario;
        this.codificadorContrasenas = codificadorContrasenas;
    }

    //MÉTODOS:

    public Usuario validarYObtenerUsuarioValido(String email, char[] contrasenaPlana, LocalDateTime fechaReferencia) {
        Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorEmail(email);
        if (usuario == null){
            throw new CredencialesInvalidasException("Credenciales Invalidas");
        }
        if (!usuario.isActivo()){
            throw new UsuarioInactivoException(
                    "Lo sentimos, NO puedes Ingresar porque NO estas Activo. Para mas información habla con el Administrador"
            );
        }
        LocalDateTime bloqueo = usuario.getBloqueadoHasta();
        if (bloqueo != null && bloqueo.isAfter(fechaReferencia)) {
            long minutosRestantes = ChronoUnit.MINUTES.between(fechaReferencia, bloqueo);
            throw new UsuarioBloqueadoException("Usuario bloqueado. Intenta de nuevo en " + minutosRestantes + " minutos.");
        }
        boolean claveCorrecta = this.codificadorContrasenas.verificar(contrasenaPlana, usuario.getHash());
        if (!claveCorrecta){
            usuario.registrarIntentoFallido();
            this.repositorioUsuario.actualizarUsuario(usuario);
            throw new CredencialesInvalidasException("Credenciales Invalidas");
        }
        usuario.limpiarIntentosFallidos();
        this.repositorioUsuario.actualizarUsuario(usuario);
        return usuario;
    }

}

