package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.excepciones.conflictos.EmailDuplicadoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioUsuario;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.util.Arrays;
import java.util.List;

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

    public Usuario obtenerUsuario(Long idUsuario){
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
        Usuario usuario = this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioUsuario.obtenerUsuarioPorEmail(email)
        );
        if (usuario != null){
            throw new EmailDuplicadoException("El correo electrónico " + email + " ya está registrado.");
        }
        String hashNuevo;
        try {
            hashNuevo = this.codificadorContrasenas.codificar(contrasenaPlana);
        } finally {
            Arrays.fill(contrasenaPlana, '\0');
        }
        Usuario usuarioNuevo = Usuario.crearNuevo(nombre, apellido, email, hashNuevo, activo);
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
            this.repositorioUsuario.insertarUsuarioNuevo(usuarioNuevo)
        );
    }

    public Usuario actualizarDatosUsuario(
            Long idUsuario, String nuevoNombre, String nuevoApellido, String nuevoEmail
    ){
        Usuario usuarioPorID = this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioUsuario.obtenerUsuarioPorId(idUsuario)
        );
        Usuario usuarioPorEmail = this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioUsuario.obtenerUsuarioPorEmail(nuevoEmail)
        );
        if (usuarioPorEmail != null && usuarioPorEmail.getEmail().equalsIgnoreCase(nuevoEmail)) {
                throw new EmailDuplicadoException("El Correo Electrónico -" + nuevoEmail + "- Ya está Registrado.");
        }
        usuarioPorID.cambiarNombre(nuevoNombre);
        usuarioPorID.cambiarApellido(nuevoApellido);
        usuarioPorID.cambiarEmail(nuevoEmail);
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            this.repositorioUsuario.actualizarDatosUsuario(usuarioPorID);
            return usuarioPorID;
        });
    }

    public void cambiarEstadoUsuario(Long idUsuario){
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
            usuario.cambiarEstado();
            this.repositorioUsuario.actualizarDatosUsuario(usuario);
        });
    }

    public void actualizarRolesUsuario(Long idUsuario, List<Rol> listaRolesActualizada){
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
            for (Rol r:usuario.getRoles()){
                usuario.quitarRol(r);
            }
            for (Rol rol:listaRolesActualizada){
                usuario.anadirRol(rol);
            }
            this.repositorioUsuario.actualizarRolesUsuario(usuario);
        });
    }

}//===================================================================================================================//

