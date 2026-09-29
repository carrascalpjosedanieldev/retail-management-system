package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.excepciones.conflictos.EmailDuplicadoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioUsuario;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ServicioUsuario {

    //ATRIBUTOS:

    private final RepositorioUsuario repositorioUsuario;

    private final CodificadorContrasenas codificadorContrasenas;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioUsuario(
            RepositorioUsuario repositorioUsuario, CodificadorContrasenas codificadorContrasenas,
            GestorTransaccional gestorTransaccional
    ) {
        this.repositorioUsuario = repositorioUsuario;
        this.codificadorContrasenas = codificadorContrasenas;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public Usuario obtenerUsuarioPorId(Long idUsuario){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioUsuario.obtenerUsuarioPorId(idUsuario)
        );
    }

    public List<Usuario> obtenerTodosLosUsuarios(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioUsuario::obtenerTodosLosUsuarios
        );
    }

    public Usuario registrarUsuario(
            String nombre, String apellido, String email, char[] contrasenaPlana, boolean activo
    ) {
        if (contrasenaPlana.length < 8){
            throw new IllegalArgumentException("La Contraseña debe tener mínimo 8 Caracteres");
        }
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()-> {
            Optional<Usuario> usuario = this.repositorioUsuario.obtenerUsuarioPorEmail(email);
            if (usuario.isPresent()){
                throw new EmailDuplicadoException("El Correo Electrónico " + email + " ya está Registrado.");
            }
            String hashNuevo;
            try {
                hashNuevo = this.codificadorContrasenas.codificar(contrasenaPlana);
            } finally {
                Arrays.fill(contrasenaPlana, '\0');
            }
            Usuario usuarioNuevo = Usuario.crearNuevo(nombre, apellido, email, hashNuevo, activo);
            return this.repositorioUsuario.insertarUsuarioNuevo(usuarioNuevo);
        });
    }

    public Usuario actualizarDatosUsuario(
            Long idUsuario, String nuevoNombre, String nuevoApellido, String nuevoEmail
    ){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()-> {
            Optional<Usuario> usuarioPorEmail = this.repositorioUsuario.obtenerUsuarioPorEmail(nuevoEmail);
            if (usuarioPorEmail.isPresent() && usuarioPorEmail.get().getIdUsuario().equals(idUsuario)) {
                throw new EmailDuplicadoException("El Correo Electrónico -" + nuevoEmail + "- Ya está Registrado.");
            }
            Usuario usuarioPorID = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
            usuarioPorID.cambiarNombre(nuevoNombre);
            usuarioPorID.cambiarApellido(nuevoApellido);
            usuarioPorID.cambiarEmail(nuevoEmail);
            this.repositorioUsuario.actualizarDatosUsuario(usuarioPorID);
            return usuarioPorID;
        });
    }

    public void cambiarEstadoUsuario(Long idUsuario){
        this.gestorTransaccional.ejecutarEnTransaccion(()-> {
            Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
            usuario.cambiarEstado();
            this.repositorioUsuario.actualizarDatosUsuario(usuario);
        });
    }

    public void actualizarRolesUsuario(Long idUsuario, List<Rol> listaRolesActualizada){
        this.gestorTransaccional.ejecutarEnTransaccion(()-> {
            Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
            usuario.getRoles().forEach(usuario::quitarRol);
            listaRolesActualizada.forEach(usuario::anadirRol);
            this.repositorioUsuario.actualizarRolesUsuario(usuario);
        });
    }

}//===================================================================================================================//

