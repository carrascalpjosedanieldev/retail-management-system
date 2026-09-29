package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.dominio.entidades.seguridad.Rol;
import RetailManagementSystem.dominio.entidades.seguridad.Usuario;
import RetailManagementSystem.dominio.excepciones.conflictos.EmailDuplicadoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.UsuarioNoEncontradoException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioUsuario;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioUsuarioTest {

    private static final long ID_USUARIO_POR_DEFECTO = 1L;

    private final Rol rolPruebas = Rol.reconstruirDesdeBD(1, "Rol Pruebas", true);

    private Usuario usuarioPruebas;

    @Mock
    private RepositorioUsuario repositorioUsuarioFalso;

    @Mock
    private CodificadorContrasenas codificadorContrasenasFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioUsuario servicioUsuario;

    @BeforeEach
    void setUp(){
        usuarioPruebas = Usuario.reconstruirDesdeBD(
                ID_USUARIO_POR_DEFECTO,
                "Nombre",
                "Apellido",
                "usuario@gmail.com",
                0,
                null,
                "$argon2id$v=19$m=65536,t=3,p=1$c2FsdGdlbmVyYWRv$hashParaTests...",
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
    void deberiaObtenerUsuarioPorIdCorrectamente(){
        //ARRANGE
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(ID_USUARIO_POR_DEFECTO)).thenReturn(usuarioPruebas);
        //ACT
        Usuario resultado = servicioUsuario.obtenerUsuarioPorId(ID_USUARIO_POR_DEFECTO);
        //ASSERT
        assertNotNull(resultado);
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(ID_USUARIO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiUsuarioNoExisteAlObtenerUsuarioPorId(){
        //ARRANGE
        long idInexistente = 99;
        String mensajeEsperado = "El Usuario de ID -" + idInexistente + "- NO Existe.";
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(idInexistente))
                .thenThrow(new UsuarioNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        UsuarioNoEncontradoException exception = assertThrows(
                UsuarioNoEncontradoException.class,
                ()-> servicioUsuario.obtenerUsuarioPorId(idInexistente)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(idInexistente);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaObtenerTodosLosUsuariosCorrectamente(){
        //ARRANGE
        List<Usuario> listaEsperada = List.of(usuarioPruebas);
        when(repositorioUsuarioFalso.obtenerTodosLosUsuarios()).thenReturn(listaEsperada);
        //ACT
        List<Usuario> resultado = servicioUsuario.obtenerTodosLosUsuarios();
        //ASSERT
        assertEquals(listaEsperada.size(), resultado.size());
        assertEquals(listaEsperada, resultado);
        verify(repositorioUsuarioFalso).obtenerTodosLosUsuarios();
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaRegistrarUsuarioCorrectamente(){
        //ARRANGE
        String nombre = "   Nuevo   ";
        String apellido = "  Apellido  ";
        String email = "   usuarioNuevo@gmail.com   ";
        char[] contrasenaPlana = {'1','2','3','4','5','6','7','8'};
        boolean activo = true;
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(email)).thenReturn(Optional.empty());
        String hashFalso = "$argon2id$v=19$m=65536,t=3,p=1$c2FsdGdlbmVyYWRv$hashfalsoejemplo...";
        when(codificadorContrasenasFalso.codificar(contrasenaPlana)).thenReturn(hashFalso);
        when(repositorioUsuarioFalso.insertarUsuarioNuevo(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        //ACT
        Usuario usuarioRegistrado = servicioUsuario.registrarUsuario(
                nombre, apellido, email, contrasenaPlana, activo
        );
        //ASSERT
        ArgumentCaptor<Usuario> usuarioCapturador = ArgumentCaptor.forClass(Usuario.class);
        verify(repositorioUsuarioFalso).insertarUsuarioNuevo(usuarioCapturador.capture());
        Usuario usuarioParaGuardar = usuarioCapturador.getValue();
        assertEquals("Nuevo", usuarioParaGuardar.getNombre());
        assertEquals("Apellido", usuarioParaGuardar.getApellido());
        assertEquals("usuarioNuevo@gmail.com", usuarioParaGuardar.getEmail());
        assertEquals(hashFalso, usuarioParaGuardar.getHash());
        assertTrue(usuarioParaGuardar.isActivo());
        assertSame(usuarioParaGuardar, usuarioRegistrado);
        for (char c : contrasenaPlana) {
            assertEquals('\0', c);
        }
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(email);
        verify(codificadorContrasenasFalso).codificar(contrasenaPlana);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, codificadorContrasenasFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiLaContrasenaTieneMenosDe8CaracteresAlRegistrarUsuario(){
        //ARRANGE
        String nombre = "   Nuevo   ";
        String apellido = "  Apellido  ";
        String email = "   usuarioNuevo@gmail.com   ";
        char[] contrasenaPlana = {'1','2','3','4','5','6','7'};
        boolean activo = true;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioUsuario.registrarUsuario(nombre, apellido, email, contrasenaPlana, activo)
        );
        assertEquals("La Contraseña debe tener mínimo 8 Caracteres", exception.getMessage());
        verifyNoInteractions(repositorioUsuarioFalso, codificadorContrasenasFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElEmailYaEstaRegistradoAlRegistrarUsuario(){
        //ARRANGE
        String nombre = "   Nuevo   ";
        String apellido = "  Apellido  ";
        String emailExistente = usuarioPruebas.getEmail();
        char[] contrasenaPlana = {'1','2','3','4','5','6','7','8'};
        boolean activo = true;
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(emailExistente)).thenReturn(Optional.of(usuarioPruebas));
        //ACT
        EmailDuplicadoException exception = assertThrows(
                EmailDuplicadoException.class,
                ()-> servicioUsuario.registrarUsuario(nombre, apellido, emailExistente, contrasenaPlana, activo)
        );
        assertEquals(
                "El Correo Electrónico " + emailExistente + " ya está Registrado.",
                exception.getMessage()
        );
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(emailExistente);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaLimpiarElArrayDeContrasenaSiFallaAlRegistrarUsuario(){
        //ARRANGE
        String nombre = "   Nuevo   ";
        String apellido = "  Apellido  ";
        String email = "   usuarioNuevo@gmail.com   ";
        char[] contrasenaPlana = {'1','2','3','4','5','6','7','8'};
        boolean activo = true;
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(email)).thenReturn(Optional.empty());
        when(codificadorContrasenasFalso.codificar(contrasenaPlana))
                .thenThrow(new RuntimeException("Error inesperado"));
        //ACT AND ASSERT
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                ()-> servicioUsuario.registrarUsuario(nombre, apellido, email, contrasenaPlana, activo)
        );
        assertEquals("Error inesperado", exception.getMessage());
        for (char c : contrasenaPlana) {
            assertEquals('\0', c);
        }
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(email);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verify(codificadorContrasenasFalso).codificar(contrasenaPlana);
        verifyNoMoreInteractions(repositorioUsuarioFalso, codificadorContrasenasFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaActualizarDatosUsuarioCorrectamente(){
        //ARRANGE
        String nuevoNombre = "  Nuevo  ";
        String nuevoApellido = "  Apellido  ";
        String nuevoEmail = "  emailNuevo@gmail.com  ";
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(nuevoEmail)).thenReturn(Optional.empty());
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(ID_USUARIO_POR_DEFECTO)).thenReturn(usuarioPruebas);
        //ACT
        Usuario resultado = servicioUsuario.actualizarDatosUsuario(
                ID_USUARIO_POR_DEFECTO, nuevoNombre, nuevoApellido, nuevoEmail
        );
        //ASSERT
        assertEquals("Nuevo", resultado.getNombre());
        assertEquals("Apellido", resultado.getApellido());
        assertEquals("emailNuevo@gmail.com", resultado.getEmail());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(ID_USUARIO_POR_DEFECTO);
        verify(repositorioUsuarioFalso).actualizarDatosUsuario(usuarioPruebas);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElEmailYaEstaRegistradoAlActualizarDatosUsuario(){
        //ARRANGE
        String nuevoNombre = "  Nuevo  ";
        String nuevoApellido = "  Apellido  ";
        String emailExistente = usuarioPruebas.getEmail();
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(emailExistente)).thenReturn(Optional.of(usuarioPruebas));
        //ACT AND ASSERT
        EmailDuplicadoException exception = assertThrows(
                EmailDuplicadoException.class,
                ()-> servicioUsuario.actualizarDatosUsuario(
                        ID_USUARIO_POR_DEFECTO, nuevoNombre, nuevoApellido, emailExistente
                )
        );
        assertEquals(
                "El Correo Electrónico -" + emailExistente + "- Ya está Registrado.",
                exception.getMessage()
        );
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(emailExistente);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElUsuarioNoExisteAlActualizarDatosUsuario(){
        //ARRANGE
        String nuevoNombre = "  Nuevo  ";
        String nuevoApellido = "  Apellido  ";
        String nuevoEmail = "  emailNuevo@gmail.com  ";
        when(repositorioUsuarioFalso.obtenerUsuarioPorEmail(nuevoEmail)).thenReturn(Optional.empty());
        long idInexistente = 99;
        String mensajeEsperado = "El Usuario de ID -" + idInexistente + "- NO Existe.";
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(idInexistente))
                .thenThrow(new UsuarioNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        UsuarioNoEncontradoException exception = assertThrows(
                UsuarioNoEncontradoException.class,
                ()-> servicioUsuario.actualizarDatosUsuario(idInexistente, nuevoNombre, nuevoApellido, nuevoEmail)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorEmail(nuevoEmail);
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(idInexistente);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaCambiarElEstadoDelUsuarioCorrectamente(){
        //ARRANGE
        boolean estadoAnterior = usuarioPruebas.isActivo();
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(ID_USUARIO_POR_DEFECTO)).thenReturn(usuarioPruebas);
        //ACT
        servicioUsuario.cambiarEstadoUsuario(ID_USUARIO_POR_DEFECTO);
        //ASSERT
        assertNotEquals(estadoAnterior, usuarioPruebas.isActivo());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(ID_USUARIO_POR_DEFECTO);
        verify(repositorioUsuarioFalso).actualizarDatosUsuario(usuarioPruebas);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElUsuarioNoExisteAlCambiarElEstadoDelUsuario(){
        //ARRANGE
        long idInexistente = 99;
        String mensajeEsperado = "El Usuario de ID -" + idInexistente + "- NO Existe.";
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(idInexistente))
                .thenThrow(new UsuarioNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        UsuarioNoEncontradoException exception = assertThrows(
                UsuarioNoEncontradoException.class,
                ()-> servicioUsuario.cambiarEstadoUsuario(idInexistente)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(idInexistente);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

    @Test
    void deberiaActualizarRolesUsuarioCorrectamente(){
        //ARRANGE
        List<Rol> rolesNuevos = List.of(rolPruebas);
        when(repositorioUsuarioFalso.obtenerUsuarioPorId(ID_USUARIO_POR_DEFECTO)).thenReturn(usuarioPruebas);
        //ACT
        servicioUsuario.actualizarRolesUsuario(ID_USUARIO_POR_DEFECTO, rolesNuevos);
        //ASSERT
        assertEquals(rolesNuevos, usuarioPruebas.getRoles());
        verify(repositorioUsuarioFalso).obtenerUsuarioPorId(ID_USUARIO_POR_DEFECTO);
        verify(repositorioUsuarioFalso).actualizarRolesUsuario(usuarioPruebas);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioUsuarioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(codificadorContrasenasFalso);
    }

}//===================================================================================================================//

