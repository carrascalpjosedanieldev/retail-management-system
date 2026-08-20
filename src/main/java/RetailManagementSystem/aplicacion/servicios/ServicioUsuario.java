package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.CredencialesInvalidasException;
import RetailManagementSystem.dominio.excepciones.conflictos.EmailDuplicadoException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioBloqueadoException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioInactivoException;
import RetailManagementSystem.dominio.puertos.RepositorioUsuario;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class ServicioUsuario {

    //ATRIBUTOS:

    private static final String CONF_MAX_INTENTOS = "SEGURIDAD_MAX_INTENTOS";

    private static final String CONF_MINUTOS_BLOQUEO = "SEGURIDAD_MINUTOS_BLOQUEO";

    private final RepositorioUsuario repositorioUsuario;

    private final CodificadorContrasenas codificadorContrasenas;

    private final ProveedorConfiguracion proveedorConfiguracion;

    //CONSTRUCTOR:

    public ServicioUsuario(RepositorioUsuario repositorioUsuario, CodificadorContrasenas codificadorContrasenas,
                           ProveedorConfiguracion proveedorConfiguracion) {
        this.repositorioUsuario = repositorioUsuario;
        this.codificadorContrasenas = codificadorContrasenas;
        this.proveedorConfiguracion = proveedorConfiguracion;
    }

    //MÉTODOS:

    public Usuario validarYObtenerUsuarioValido(String email, char[] contrasenaPlana, LocalDateTime fechaReferencia) {
        Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorEmail(email)
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales Inválidas."));
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
        boolean claveCorrecta;
        try {
            claveCorrecta = this.codificadorContrasenas.verificar(contrasenaPlana, usuario.getHash());
        } finally {
            Arrays.fill(contrasenaPlana, '\0');
        }
        if (!claveCorrecta){
            int maxIntentos = Integer.parseInt(
                    this.proveedorConfiguracion.obtenerValorConfiguracion(CONF_MAX_INTENTOS)
            );
            int minutosBloqueo = Integer.parseInt(
                    this.proveedorConfiguracion.obtenerValorConfiguracion(CONF_MINUTOS_BLOQUEO)
            );
            usuario.registrarIntentoFallido(maxIntentos, minutosBloqueo, fechaReferencia);
            this.repositorioUsuario.actualizarDatosLoginUsuario(usuario);
            throw new CredencialesInvalidasException("Credenciales Invalidas");
        }
        usuario.limpiarIntentosFallidosYBloqueo();
        this.repositorioUsuario.actualizarDatosLoginUsuario(usuario);
        return usuario;
    }

    public Usuario registrarUsuario(String nombre, String apellido, String email, char[] contrasenaPlana, boolean activo){
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
    }

    public void actualizarDatosUsuario(
            int idUsuario, String nuevoNombre, String nuevoApellido, String nuevoEmail, boolean activo
    ){
        Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
        if (!usuario.getEmail().equalsIgnoreCase(nuevoEmail)) {
            this.repositorioUsuario.obtenerUsuarioPorEmail(nuevoEmail)
                    .ifPresent(u -> {
                        throw new EmailDuplicadoException("El Correo Electrónico -" + nuevoApellido + "- Ya está Registrado.");
                    });
        }
        usuario.cambiarNombre(nuevoNombre);
        usuario.cambiarApellido(nuevoApellido);
        usuario.cambiarEmail(nuevoEmail);
        usuario.cambiarEstado(activo);
        this.repositorioUsuario.actualizarDatosLoginUsuario(usuario);
    }

    public String restablecerContrasenaPorAdmin(int idUsuario) {
        Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
        String caracteresPermitidos = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder claveTemporal = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            claveTemporal.append(caracteresPermitidos.charAt(random.nextInt(caracteresPermitidos.length())));
        }
        char[] clavePlana = claveTemporal.toString().toCharArray();
        String hashTemporal;
        try {
            hashTemporal = this.codificadorContrasenas.codificar(clavePlana);
        } finally {
            Arrays.fill(clavePlana, '\0');
        }
        usuario.asignarContrasenaTemporal(hashTemporal);
        this.repositorioUsuario.actualizarSeguridad(usuario);
        return claveTemporal.toString();
    }

    public void cambiarContrasenaDefinitiva(int idUsuario, char[] nuevaContrasenaPlana) {
        if (nuevaContrasenaPlana == null || nuevaContrasenaPlana.length < 8) {
            throw new IllegalArgumentException("La Nueva Contraseña debe tener al menos 8 Caracteres.");
        }
        Usuario usuario = this.repositorioUsuario.obtenerUsuarioPorId(idUsuario);
        String nuevoHash;
        try {
            nuevoHash = this.codificadorContrasenas.codificar(nuevaContrasenaPlana);
        } finally {
            Arrays.fill(nuevaContrasenaPlana, '\0');
        }
        usuario.establecerContrasenaDefinitiva(nuevoHash);
        this.repositorioUsuario.actualizarSeguridad(usuario);
    }

}//===================================================================================================================//

