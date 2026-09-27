package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.enums.ClaveConfiguracion;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.CredencialesInvalidasException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioBloqueadoException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioInactivoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioUsuario;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

public class ServicioLogin {

    private static final String HASH_FALSO = "$argon2id$v=19$m=65536,t=3,p=1$c2FsdGdlbmVyYWRv$hashfalsoejemplo...";

    //ATRIBUTOS:

    private final RepositorioUsuario repositorioUsuario;

    private final CodificadorContrasenas codificadorContrasenas;

    private final ProveedorConfiguracion proveedorConfiguracion;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioLogin(
            RepositorioUsuario repositorioUsuario, CodificadorContrasenas codificadorContrasenas,
            ProveedorConfiguracion proveedorConfiguracion, GestorTransaccional gestorTransaccional
    ) {
        this.repositorioUsuario = repositorioUsuario;
        this.codificadorContrasenas = codificadorContrasenas;
        this.proveedorConfiguracion = proveedorConfiguracion;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public Usuario validarIngresoYObtenerUsuarioValido(
            String email, char[] contrasenaPlana, LocalDateTime fechaReferencia
    ) {
        Optional<Usuario> usuarioOptional = this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioUsuario.obtenerUsuarioPorEmail(email)
        );
        String hashAVerificar = usuarioOptional.map(Usuario::getHash).orElse(HASH_FALSO);
        boolean claveCorrecta;
        try {
            claveCorrecta = this.codificadorContrasenas.verificar(contrasenaPlana, hashAVerificar);
        } finally {
            Arrays.fill(contrasenaPlana, '\0');
        }
        if (usuarioOptional.isEmpty() || !claveCorrecta) {
            usuarioOptional.ifPresent(usuarioValor -> this.gestorTransaccional.ejecutarEnTransaccion(() ->
                    registrarFalloYPosibleBloqueo(usuarioValor, fechaReferencia)
            ));
            throw new CredencialesInvalidasException("Credenciales Inválidas.");
        }
        Usuario usuarioValido = usuarioOptional.get();
        validarBloqueoTemporal(usuarioValido, fechaReferencia);
        if (!usuarioValido.isActivo()){
            throw new UsuarioInactivoException("Lo sentimos, NO puedes Ingresar porque NO estas Activo. " +
                    "Para mas información habla con el Administrador");
        }
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            usuarioValido.limpiarIntentosFallidosYBloqueo();
            this.repositorioUsuario.actualizarDatosLoginUsuario(usuarioValido);
            return usuarioValido;
        });
    }

    private void registrarFalloYPosibleBloqueo(Usuario usuario, LocalDateTime fechaReferencia){
        int maxIntentos = Integer.parseInt(
              this.proveedorConfiguracion.obtenerValorConfiguracion(ClaveConfiguracion.MAX_INTENTOS_LOGIN.getClaveBD())
        );
        int minutosBloqueo = Integer.parseInt(
              this.proveedorConfiguracion.obtenerValorConfiguracion(ClaveConfiguracion.MINUTOS_BLOQUEO.getClaveBD())
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

}//===================================================================================================================//

