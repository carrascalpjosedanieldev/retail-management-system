package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.aplicacion.servicios.seguridad.ServicioLogin;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.enums.ClaveConfiguracion;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.CredencialesInvalidasException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioBloqueadoException;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.UsuarioInactivoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.UsuarioNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioConfiguracion;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioUsuario;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ServicioLoginTest {

    private static final String EMAIL_POR_DEFECTO = "usuario@gmail.com";

    private static final String HASH_POR_DEFECTO = "$argon2id$v=19$m=65536,t=3,p=1$c2FsdGdlbmVyYWRv$hashParaTests...";

    private static final String HASH_FALSO = "$argon2id$v=19$m=65536,t=3,p=1$c2FsdGdlbmVyYWRv$hashfalsoejemplo...";

    private static final char[] CONTRASENA_POR_DEFECTO = {'1','2','3','4','a','b','c','d','#'};

    private Usuario usuarioPruebas;

    private static final String MAX_INTENTOS_TEST_POR_DEFECTO = "3";

    private static final String MINUTOS_BLOQUEO_POR_DEFECTO = "15";

    @Mock
    private RepositorioConfiguracion repositorioConfiguracion;

    @Mock
    private RepositorioUsuario repositorioUsuarioFalso;

    @Mock
    private CodificadorContrasenas codificadorContrasenasFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioLogin servicioLogin;

    @BeforeEach
    void setUp(){
        usuarioPruebas = Usuario.reconstruirDesdeBD(
                1L,
                "Usuario Nombre",
                "Usuario Apellido",
                EMAIL_POR_DEFECTO,
                0,
                null,
                HASH_POR_DEFECTO,
                true,
                false
        );
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionConRetorno(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionDeLectura(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().doAnswer(invocation -> {
            OperacionTransaccional operacion = invocation.getArgument(0);
            operacion.ejecutar();
            return null;
        }).when(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    //TESTS

    @Test
    void deberiaValidarIngresoYObtenerUsuarioValidoCorrectamente(){
        //ARRANGE
        LocalDateTime fecha = LocalDateTime.now();
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO)).thenReturn(Optional.of(usuarioPruebas));
        when(codificadorContrasenasFalso.verificar(CONTRASENA_POR_DEFECTO, HASH_POR_DEFECTO)).thenReturn(true);
        //ACT
        Usuario usuario =
                servicioLogin.validarIngresoYObtenerUsuarioValido(EMAIL_POR_DEFECTO, CONTRASENA_POR_DEFECTO, fecha);
        //ASSERT
        assertEquals(0, usuario.getIntentosFallidos());
        assertNull(usuario.getBloqueadoHasta());
        for (char c : CONTRASENA_POR_DEFECTO) {
            assertEquals('\0', c);
        }
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO);
        verify(codificadorContrasenasFalso).verificar(CONTRASENA_POR_DEFECTO, HASH_POR_DEFECTO);
        verify(repositorioUsuarioFalso).actualizarDatosLoginUsuario(usuario);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaLanzarExcepcionDeCredencialesInvalidasSiElUsuarioNoExisteAlValidarIngreso(){
        //ARRANGE
        LocalDateTime fecha = LocalDateTime.now();
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO)).thenReturn(Optional.empty());
        when(codificadorContrasenasFalso.verificar(CONTRASENA_POR_DEFECTO, HASH_FALSO)).thenReturn(false);
        //ACT AND ASSERT
        CredencialesInvalidasException exception = assertThrows(
                CredencialesInvalidasException.class,
                ()-> servicioLogin.validarIngresoYObtenerUsuarioValido(EMAIL_POR_DEFECTO, CONTRASENA_POR_DEFECTO, fecha)
        );
        assertEquals("Credenciales Inválidas.", exception.getMessage());
        for (char c : CONTRASENA_POR_DEFECTO) {
            assertEquals('\0', c);
        }
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(codificadorContrasenasFalso).verificar(CONTRASENA_POR_DEFECTO, HASH_FALSO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionDeCredencialesInvalidasYRegistrarFalloSiLaClaveEsIncorrectaAlValidarIngreso(){
        //ARRANGE
        char[] contrasenaIncorrecta = {'I','N','C','O','R','R','E','C','T','A'};
        LocalDateTime fecha = LocalDateTime.now();
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO)).thenReturn(Optional.of(usuarioPruebas));
        when(codificadorContrasenasFalso.verificar(contrasenaIncorrecta, HASH_POR_DEFECTO)).thenReturn(false);
        when(repositorioConfiguracion.obtenerValorConfiguracion(ClaveConfiguracion.MAX_INTENTOS_LOGIN.getClaveBD()))
                .thenReturn(MAX_INTENTOS_TEST_POR_DEFECTO);
        when(repositorioConfiguracion.obtenerValorConfiguracion(ClaveConfiguracion.MINUTOS_BLOQUEO.getClaveBD()))
                .thenReturn(MINUTOS_BLOQUEO_POR_DEFECTO);
        //ACT AND ASSERT
        CredencialesInvalidasException exception = assertThrows(
                CredencialesInvalidasException.class,
                ()-> servicioLogin.validarIngresoYObtenerUsuarioValido(EMAIL_POR_DEFECTO, contrasenaIncorrecta, fecha)
        );
        assertEquals("Credenciales Inválidas.", exception.getMessage());
        assertEquals(1, usuarioPruebas.getIntentosFallidos());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO);
        verify(repositorioUsuarioFalso).actualizarDatosLoginUsuario(usuarioPruebas);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(codificadorContrasenasFalso).verificar(contrasenaIncorrecta, HASH_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
        verify(repositorioConfiguracion)
                .obtenerValorConfiguracion(ClaveConfiguracion.MAX_INTENTOS_LOGIN.getClaveBD());
        verify(repositorioConfiguracion)
                .obtenerValorConfiguracion(ClaveConfiguracion.MINUTOS_BLOQUEO.getClaveBD());
        verifyNoMoreInteractions(repositorioConfiguracion);
    }

    @Test
    void deberiaLanzarExcepcionDeUsuarioBloqueadoSiElBloqueoAunEstaVigenteAlValidarIngreso(){
        //ASSERT
        LocalDateTime fechaReferencia = LocalDateTime.now();
        int minutosBloqueo = 15;
        usuarioPruebas.registrarIntentoFallido(1, minutosBloqueo, fechaReferencia);
        long minutosRestantes = ChronoUnit.MINUTES.between(fechaReferencia, usuarioPruebas.getBloqueadoHasta());
        String mensajeEsperado = "Usuario Bloqueado. Intenta de Nuevo en " + minutosRestantes + " Minutos.";
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO)).thenReturn(Optional.of(usuarioPruebas));
        when(codificadorContrasenasFalso.verificar(CONTRASENA_POR_DEFECTO, HASH_POR_DEFECTO)).thenReturn(true);
        // ACT & ASSERT
        UsuarioBloqueadoException exception = assertThrows(
                UsuarioBloqueadoException.class,
                () -> servicioLogin.validarIngresoYObtenerUsuarioValido(
                        EMAIL_POR_DEFECTO, CONTRASENA_POR_DEFECTO, fechaReferencia
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        for (char c : CONTRASENA_POR_DEFECTO) {
            assertEquals('\0', c);
        }
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(codificadorContrasenasFalso).verificar(CONTRASENA_POR_DEFECTO, HASH_POR_DEFECTO);
        verifyNoMoreInteractions(codificadorContrasenasFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionDeUsuarioInactivoSiElUsuarioEstaInactivoAlValidarIngreso(){
        //ASSERT
        LocalDateTime fecha = LocalDateTime.now();
        usuarioPruebas.cambiarEstado();
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO)).thenReturn(Optional.of(usuarioPruebas));
        when(codificadorContrasenasFalso.verificar(CONTRASENA_POR_DEFECTO, HASH_POR_DEFECTO)).thenReturn(true);
        //ACT AND ASSERT
        UsuarioInactivoException exception = assertThrows(
                UsuarioInactivoException.class,
                ()-> servicioLogin.validarIngresoYObtenerUsuarioValido(EMAIL_POR_DEFECTO, CONTRASENA_POR_DEFECTO, fecha)
        );
        assertEquals(
                "Lo sentimos, NO puedes Ingresar porque NO estas Activo. " +
                "Para mas información habla con el Administrador",
                exception.getMessage()
        );
        for (char c : CONTRASENA_POR_DEFECTO) {
            assertEquals('\0', c);
        }
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(codificadorContrasenasFalso).verificar(CONTRASENA_POR_DEFECTO, HASH_POR_DEFECTO);
        verifyNoMoreInteractions(codificadorContrasenasFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaPermitirElIngresoYLimpiarFallosSiElBloqueoYaExpiroAlValidarIngreso(){
        //ARRANGE
        LocalDateTime fecha = LocalDateTime.now();
        int minutosBloqueo = 5;
        LocalDateTime fechaReferencia = fecha.plusMinutes(minutosBloqueo + 1);
        usuarioPruebas.registrarIntentoFallido(1, minutosBloqueo, fecha);
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO)).thenReturn(Optional.of(usuarioPruebas));
        when(codificadorContrasenasFalso.verificar(CONTRASENA_POR_DEFECTO, HASH_POR_DEFECTO)).thenReturn(true);
        //ACT
        Usuario usuario = servicioLogin.validarIngresoYObtenerUsuarioValido(
                EMAIL_POR_DEFECTO, CONTRASENA_POR_DEFECTO, fechaReferencia
        );
        //ASSERT
        assertEquals(0, usuario.getIntentosFallidos());
        assertNull(usuario.getBloqueadoHasta());
        for (char c : CONTRASENA_POR_DEFECTO) {
            assertEquals('\0', c);
        }
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(EMAIL_POR_DEFECTO);
        verify(repositorioUsuarioFalso).actualizarDatosLoginUsuario(usuarioPruebas);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(codificadorContrasenasFalso).verificar(CONTRASENA_POR_DEFECTO, HASH_POR_DEFECTO);
        verifyNoMoreInteractions(codificadorContrasenasFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaRestablecerContrasenaPorAdminYRetornarClavePlanaCorrectamente(){
        //ARRANGE
        Long idUsuario = 1L;
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(idUsuario)).thenReturn(usuarioPruebas);
        String hashGenerado = "HASH_TEMPORAL_TEST_123";
        when(codificadorContrasenasFalso.codificar(any(char[].class))).thenReturn(hashGenerado);
        //ACT
        char[] contrasenaTemporal = servicioLogin.restablecerContrasenaPorAdmin(idUsuario);
        //ASSERT
        assertNotNull(contrasenaTemporal);
        assertEquals(6, contrasenaTemporal.length);
        assertEquals(hashGenerado, usuarioPruebas.getHash());
        assertTrue(usuarioPruebas.isDebeCambiarContrasena());
        assertEquals(0, usuarioPruebas.getIntentosFallidos());
        assertNull(usuarioPruebas.getBloqueadoHasta());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(idUsuario);
        verify(repositorioUsuarioFalso).actualizarSeguridad(usuarioPruebas);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(codificadorContrasenasFalso).codificar(any(char[].class));
        verifyNoMoreInteractions(codificadorContrasenasFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElUsuarioNoExisteAlRestablecerContrasenaPorAdmin(){
        //ARRANGE
        Long idUsuario = 1L;
        String mensajeEsperado = "El Usuario de ID -" + idUsuario + "- NO Existe.";
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(idUsuario))
                .thenThrow(new UsuarioNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        UsuarioNoEncontradoException exception = assertThrows(
                UsuarioNoEncontradoException.class,
                ()-> servicioLogin.restablecerContrasenaPorAdmin(idUsuario)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(idUsuario);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaCambiarContrasenaDefinitivaCorrectamente(){
        //ARRANGE
        Long idUsuario = 1L;
        char[] contrasenaFinal = {'a','b','c','d','e','1','2','3'};
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(idUsuario)).thenReturn(usuarioPruebas);
        String hashNuevo = "HASH_NUEVO_TEST";
        when(codificadorContrasenasFalso.codificar(contrasenaFinal)).thenReturn(hashNuevo);
        //ACT
        servicioLogin.cambiarContrasenaDefinitiva(idUsuario, contrasenaFinal);
        //ASSERT
        assertEquals(hashNuevo, usuarioPruebas.getHash());
        assertFalse(usuarioPruebas.isDebeCambiarContrasena());
        for (char c : contrasenaFinal) {
            assertEquals('\0', c);
        }
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(idUsuario);
        verify(repositorioUsuarioFalso).actualizarSeguridad(usuarioPruebas);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(codificadorContrasenasFalso).codificar(contrasenaFinal);
        verifyNoMoreInteractions(codificadorContrasenasFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiLaContrasenaEsNulaAlCambiarContrasenaDefinitiva(){
        //ARRANGE
        long idUsuario = 1L;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioLogin.cambiarContrasenaDefinitiva(idUsuario, null)
        );
        assertEquals("La Nueva Contraseña debe tener al menos 8 Caracteres.", exception.getMessage());
        verifyNoInteractions(
                gestorTransaccionalFalso, repositorioUsuarioFalso, codificadorContrasenasFalso
        );
    }

    @Test
    void deberiaLanzarExcepcionSiLaContrasenaEsMenorAOchoCaracteresAlCambiarContrasenaDefinitiva(){
        //ARRANGE
        long idUsuario = 1L;
        char[] contrasenaFinal = {'1','2','3','4','5','6','7'};
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioLogin.cambiarContrasenaDefinitiva(idUsuario, contrasenaFinal)
        );
        assertEquals("La Nueva Contraseña debe tener al menos 8 Caracteres.", exception.getMessage());
        verifyNoInteractions(
                gestorTransaccionalFalso, repositorioUsuarioFalso, codificadorContrasenasFalso
        );
    }

    @Test
    void deberiaLanzarExcepcionSiElUsuarioNoExisteAlCambiarContrasenaDefinitiva(){
        //ARRANGE
        long idUsuarioFalso = 999L;
        char[] contrasenaFinal = {'a','b','c','d','e','1','2','3'};
        String mensajeEsperado = "El Usuario de ID -" + idUsuarioFalso + "- NO Existe.";
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(idUsuarioFalso))
                .thenThrow(new UsuarioNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        UsuarioNoEncontradoException exception = assertThrows(
                UsuarioNoEncontradoException.class,
                ()-> servicioLogin.cambiarContrasenaDefinitiva(idUsuarioFalso, contrasenaFinal)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(idUsuarioFalso);
        verifyNoMoreInteractions(repositorioUsuarioFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

}//===================================================================================================================//

