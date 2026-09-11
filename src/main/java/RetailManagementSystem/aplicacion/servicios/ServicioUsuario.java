package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.CredencialesInvalidasException;
import RetailManagementSystem.dominio.excepciones.conflictos.EmailDuplicadoException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioBloqueadoException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioInactivoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioUsuario;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class ServicioUsuario {

    private static final String CONF_MAX_INTENTOS = "SEGURIDAD_MAX_INTENTOS";

    private static final String CONF_MINUTOS_BLOQUEO = "SEGURIDAD_MINUTOS_BLOQUEO";

    private static final String HASH_FALSO = "$argon2id$v=19$m=65536,t=3,p=1$c2FsdGdlbmVyYWRv$hashfalsoejemplo...";

    //ATRIBUTOS:

    private final RepositorioUsuario repositorioUsuario;

    private final CodificadorContrasenas codificadorContrasenas;

    private final ProveedorConfiguracion proveedorConfiguracion;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioUsuario(
            RepositorioUsuario repositorioUsuario, CodificadorContrasenas codificadorContrasenas,
            ProveedorConfiguracion proveedorConfiguracion, GestorTransaccional gestorTransaccional
    ) {
        this.repositorioUsuario = repositorioUsuario;
        this.codificadorContrasenas = codificadorContrasenas;
        this.proveedorConfiguracion = proveedorConfiguracion;
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

    public Usuario validarYObtenerUsuarioValido(String email, char[] contrasenaPlana, LocalDateTime fechaReferencia) {
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorEmail(email).orElse(null);
            String hashAVerificar = usuario != null ? usuario.getHash() : HASH_FALSO;
            boolean claveCorrecta;
            try {
                claveCorrecta = this.codificadorContrasenas.verificar(contrasenaPlana, hashAVerificar);
            } finally {
                Arrays.fill(contrasenaPlana, '\0');
            }
            if (usuario == null || !claveCorrecta) {
                if (usuario != null) {
                    registrarFalloYPosibleBloqueo(usuario, fechaReferencia);
                    validarBloqueoTemporal(usuario, fechaReferencia);
                }
                throw new CredencialesInvalidasException("Credenciales Inválidas.");
            }
            if (!usuario.isActivo()){
                throw new UsuarioInactivoException(
                        "Lo sentimos, NO puedes Ingresar porque NO estas Activo. Para mas información habla con el Administrador"
                );
            }
            usuario.limpiarIntentosFallidosYBloqueo();
            this.repositorioUsuario.actualizarDatosLoginUsuario(usuario);
            return usuario;
        });
    }

    private void registrarFalloYPosibleBloqueo(Usuario usuario, LocalDateTime fechaReferencia){
        int maxIntentos = Integer.parseInt(
                this.proveedorConfiguracion.obtenerValorConfiguracion(CONF_MAX_INTENTOS)
        );
        int minutosBloqueo = Integer.parseInt(
                this.proveedorConfiguracion.obtenerValorConfiguracion(CONF_MINUTOS_BLOQUEO)
        );
        usuario.registrarIntentoFallido(maxIntentos, minutosBloqueo, fechaReferencia);
        this.repositorioUsuario.actualizarDatosLoginUsuario(usuario);
    }

    private void validarBloqueoTemporal(Usuario usuario, LocalDateTime fechaReferencia){
        LocalDateTime bloqueo = usuario.getBloqueadoHasta();
        if (bloqueo != null && bloqueo.isAfter(fechaReferencia)) {
            long minutosRestantes = ChronoUnit.MINUTES.between(fechaReferencia, bloqueo);
            throw new UsuarioBloqueadoException(
                    "Usuario Bloqueado. Intenta de Nuevo en " + minutosRestantes + " Minutos."
            );
        }
    }


    public Usuario registrarUsuario(
            String nombre, String apellido, String email, char[] contrasenaPlana, boolean activo
    ) {
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            this.repositorioUsuario.obtenerUsuarioPorEmail(email)
                    .ifPresent(u -> {
                        throw new EmailDuplicadoException("El correo electrónico " + email + " ya está registrado.");
                    });
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
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
            if (!usuario.getEmail().equalsIgnoreCase(nuevoEmail)) {
                this.repositorioUsuario.obtenerUsuarioPorEmail(nuevoEmail)
                        .ifPresent(u -> {
                            throw new EmailDuplicadoException("El Correo Electrónico -" + nuevoEmail + "- Ya está Registrado.");
                        });
            }
            usuario.cambiarNombre(nuevoNombre);
            usuario.cambiarApellido(nuevoApellido);
            usuario.cambiarEmail(nuevoEmail);
            this.repositorioUsuario.actualizarDatosUsuario(usuario);
            return usuario;
        });
    }


    public void cambiarEstadoUsuario(Long idUsuario){
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
            if (usuario.isActivo()){
                usuario.desactivarUsuario();
            } else {
                usuario.activarUsuario();
            }
            this.repositorioUsuario.actualizarDatosUsuario(usuario);
        });
    }


    public char[] restablecerContrasenaPorAdmin(Long idUsuario) {
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
            String caracteresPermitidos = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
            SecureRandom random = new SecureRandom();
            char[] claveOriginal = new char[6];
            for (int i = 0; i < 6; i++) {
                claveOriginal[i] = caracteresPermitidos.charAt(random.nextInt(caracteresPermitidos.length()));
            }
            char[] copiaParaServicio = claveOriginal.clone();
            String hashTemporal;
            try {
                hashTemporal = this.codificadorContrasenas.codificar(copiaParaServicio);
            } finally {
                Arrays.fill(copiaParaServicio, '\0');
            }
            usuario.asignarContrasenaTemporal(hashTemporal);
            this.repositorioUsuario.actualizarSeguridad(usuario);
            return claveOriginal;
        });
    }


    public void cambiarContrasenaDefinitiva(Long idUsuario, char[] nuevaContrasenaPlana) {
        if (nuevaContrasenaPlana == null || nuevaContrasenaPlana.length < 8) {
            throw new IllegalArgumentException("La Nueva Contraseña debe tener al menos 8 Caracteres.");
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
            String nuevoHash;
            try {
                nuevoHash = this.codificadorContrasenas.codificar(nuevaContrasenaPlana);
            } finally {
                Arrays.fill(nuevaContrasenaPlana, '\0');
            }
            usuario.establecerContrasenaDefinitiva(nuevoHash);
            this.repositorioUsuario.actualizarSeguridad(usuario);
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

