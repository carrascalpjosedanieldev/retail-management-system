package RetailManagementSystem.dominio.entidades.seguridad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    private static final Long ID_USUARIO_POR_DEFECTO = 1L;

    private static final String NOMBRE_POR_DEFECTO = "Usuario normal";

    private static final String APELLIDO_POR_DEFECTO = "Apellido normal";

    private static final String EMAIL_POR_DEFECTO = "usuarionormal@gmail.com";

    private static final String HASH_POR_DEFECTO = "$argon2id$v=19$m=65536,t=3,p=1$c2FsdGdlbmVyYWRv$hashFalso...";

    private final Rol rolPruebas = Rol.reconstruirDesdeBD(1, "Rol Pruebas", true);

    private final Permiso permisoPruebas =
            Permiso.reconstruirDesdeBD(
                    1,
                    "Permiso pruebas",
                    "Descripción normal",
                    "Modulo",
                    true
            );

    private Usuario usuarioPruebas;

    @BeforeEach
    void setUp(){
        usuarioPruebas = Usuario.reconstruirDesdeBD(
                ID_USUARIO_POR_DEFECTO,
                NOMBRE_POR_DEFECTO,
                APELLIDO_POR_DEFECTO,
                EMAIL_POR_DEFECTO,
                0,
                null,
                HASH_POR_DEFECTO,
                true,
                false
        );
    }

    //TESTS CONSTRUCTORES

    @Test
    void deberiaReconstruirDesdeBDCorrectamente() {
        // ARRANGE
        LocalDateTime fechaBloqueo = LocalDateTime.now();
        // ACT
        Usuario usuario = Usuario.reconstruirDesdeBD(
                2L,
                "  Juan  ",
                "  Perez  ",
                " juan@mail.com ",
                3,
                fechaBloqueo,
                HASH_POR_DEFECTO,
                false,
                true
        );
        // ASSERT
        assertEquals(2L, usuario.getIdUsuario());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Perez", usuario.getApellido());
        assertEquals("juan@mail.com", usuario.getEmail());
        assertEquals(3, usuario.getIntentosFallidos());
        assertEquals(fechaBloqueo, usuario.getBloqueadoHasta());
        assertEquals(HASH_POR_DEFECTO, usuario.getHash());
        assertFalse(usuario.isActivo());
        assertTrue(usuario.isDebeCambiarContrasena());
        assertNotNull(usuario.getRoles());
        assertTrue(usuario.getRoles().isEmpty());
        assertNotNull(usuario.getPermisosCacheados());
        assertTrue(usuario.getPermisosCacheados().isEmpty());
    }

    @Test
    void deberiaCrearNuevoCorrectamente() {
        // ACT
        Usuario usuario = Usuario.crearNuevo(
                "  Ana  ",
                "  Gomez  ",
                " ana@mail.com ",
                HASH_POR_DEFECTO,
                true
        );
        // ASSERT
        assertNull(usuario.getIdUsuario());
        assertEquals("Ana", usuario.getNombre());
        assertEquals("Gomez", usuario.getApellido());
        assertEquals("ana@mail.com", usuario.getEmail());
        assertEquals(0, usuario.getIntentosFallidos());
        assertNull(usuario.getBloqueadoHasta());
        assertEquals(HASH_POR_DEFECTO, usuario.getHash());
        assertTrue(usuario.isActivo());
        assertTrue(usuario.isDebeCambiarContrasena());
        assertNotNull(usuario.getRoles());
        assertTrue(usuario.getRoles().isEmpty());
        assertNotNull(usuario.getPermisosCacheados());
        assertTrue(usuario.getPermisosCacheados().isEmpty());
    }

    @ParameterizedTest
    @CsvSource(value = {"'   '", "''", "null"}, nullValues = "null")
    void deberiaLanzarExcepcionSiNombreEsInvalidoAlConstruir(String nombreInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Usuario.crearNuevo(
                        nombreInvalido,
                        APELLIDO_POR_DEFECTO,
                        EMAIL_POR_DEFECTO,
                        HASH_POR_DEFECTO,
                        true
                )
        );
        assertEquals("Nombre del Usuario Vacío", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"'   '", "''", "null"}, nullValues = "null")
    void deberiaLanzarExcepcionSiApellidoEsInvalidoAlConstruir(String apellidoInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Usuario.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        apellidoInvalido,
                        EMAIL_POR_DEFECTO,
                        HASH_POR_DEFECTO,
                        true
                )
        );
        assertEquals("Apellido del Usuario Vacío", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"'   '", "''", "null"}, nullValues = "null")
    void deberiaLanzarExcepcionSiEmailEsInvalidoAlConstruir(String emailInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Usuario.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        APELLIDO_POR_DEFECTO,
                        emailInvalido,
                        HASH_POR_DEFECTO,
                        true
                )
        );
        assertEquals("Email del Usuario Vacío", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"'   '", "''", "null"}, nullValues = "null")
    void deberiaLanzarExcepcionSiHashEsInvalidoAlConstruir(String hashInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Usuario.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        APELLIDO_POR_DEFECTO,
                        EMAIL_POR_DEFECTO,
                        hashInvalido,
                        true
                )
        );
        assertEquals("Hash del Usuario Invalido", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"-1", "null"}, nullValues = "null")
    void deberiaLanzarExcepcionSiIntentosFallidosSonInvalidosAlConstruir(Integer intentosInvalidos) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Usuario.reconstruirDesdeBD(
                        ID_USUARIO_POR_DEFECTO,
                        NOMBRE_POR_DEFECTO,
                        APELLIDO_POR_DEFECTO,
                        EMAIL_POR_DEFECTO,
                        intentosInvalidos,
                        null,
                        HASH_POR_DEFECTO,
                        true,
                        false
                )
        );
        assertEquals("Intentos Fallidos del Usuario Inválidos", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEstadoActivoEsNuloAlConstruir() {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Usuario.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        APELLIDO_POR_DEFECTO,
                        EMAIL_POR_DEFECTO,
                        HASH_POR_DEFECTO,
                        null
                )
        );
        assertEquals("El Estado del Usuario es Obligatorio", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiDebeCambiarContrasenaEsNuloAlConstruir() {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Usuario.reconstruirDesdeBD(
                        ID_USUARIO_POR_DEFECTO,
                        NOMBRE_POR_DEFECTO,
                        APELLIDO_POR_DEFECTO,
                        EMAIL_POR_DEFECTO,
                        0,
                        null,
                        HASH_POR_DEFECTO,
                        true,
                        null
                )
        );
        assertEquals("El Dato Asignado sobre si debe Cambiar Contraseña o NO es Obligatorio", exception.getMessage());
    }

    //TESTS MODIFICAR

    @Test
    void deberiaCambiarNombreCorrectamente() {
        // ARRANGE
        String nombreNuevo = "  Carlos  ";
        // ACT
        usuarioPruebas.cambiarNombre(nombreNuevo);
        // ASSERT
        assertEquals("Carlos", usuarioPruebas.getNombre());
    }

    @ParameterizedTest
    @CsvSource(value = {"'   '", "''", "null"}, nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarNombreEsInvalido(String nombreInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioPruebas.cambiarNombre(nombreInvalido)
        );
        assertEquals("Nombre del Usuario Vacío", exception.getMessage());
    }

    @Test
    void deberiaCambiarApellidoCorrectamente() {
        // ARRANGE
        String apellidoNuevo = "  Ramirez  ";
        // ACT
        usuarioPruebas.cambiarApellido(apellidoNuevo);
        // ASSERT
        assertEquals("Ramirez", usuarioPruebas.getApellido());
    }

    @ParameterizedTest
    @CsvSource(value = {"'   '", "''", "null"}, nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarApellidoEsInvalido(String apellidoInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioPruebas.cambiarApellido(apellidoInvalido)
        );
        assertEquals("Apellido del Usuario Vacío", exception.getMessage());
    }

    @Test
    void deberiaCambiarEmailCorrectamente() {
        // ARRANGE
        String emailNuevo = "  nuevoemail@gmail.com  ";
        // ACT
        usuarioPruebas.cambiarEmail(emailNuevo);
        // ASSERT
        assertEquals("nuevoemail@gmail.com", usuarioPruebas.getEmail());
    }

    @ParameterizedTest
    @CsvSource(value = {"'   '", "''", "null"}, nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarEmailEsInvalido(String emailInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioPruebas.cambiarEmail(emailInvalido)
        );
        assertEquals("Email del Usuario Vacío", exception.getMessage());
    }

    @Test
    void deberiaCambiarEstadoCorrectamente() {
        // ARRANGE
        boolean estadoAnterior = usuarioPruebas.isActivo();
        // ACT
        usuarioPruebas.cambiarEstado();
        // ASSERT
        assertNotEquals(estadoAnterior, usuarioPruebas.isActivo());
    }

    //TESTS DATOS SEGURIDAD

    @Test
    void deberiaRegistrarUnIntentoFallidoCorrectamente(){
        //ARRANGE
        int maxIntentosFallidos = 3;
        int minutosDeBloqueo = 10;
        LocalDateTime fechaReferencia = LocalDateTime.now();
        //ACT
        usuarioPruebas.registrarIntentoFallido(maxIntentosFallidos, minutosDeBloqueo, fechaReferencia);
        //ASSERT
        assertEquals(1, usuarioPruebas.getIntentosFallidos());
    }

    @Test
    void deberiaRegistrarIntentosFallidosYBloquearUsuarioCorrectamente(){
        //ARRANGE
        int maxIntentosFallidos = 1;
        int minutosDeBloqueo = 10;
        LocalDateTime fechaReferencia = LocalDateTime.now();
        LocalDateTime fechaBloqueo = fechaReferencia.plusMinutes(minutosDeBloqueo);
        //ACT
        usuarioPruebas.registrarIntentoFallido(maxIntentosFallidos, minutosDeBloqueo, fechaReferencia);
        //ASSERT
        assertEquals(1, usuarioPruebas.getIntentosFallidos());
        assertEquals(fechaBloqueo, usuarioPruebas.getBloqueadoHasta());
    }

    @ParameterizedTest
    @CsvSource({"0", "-1"})
    void deberiaLanzarExcepcionSiMaxIntentosFallidosEsInvalidoAlRegistrarIntentoFallido(
            int maxIntentosFallidosInvalidos
    ) {
        //ARRANGE
        int minutosDeBloqueo = 10;
        LocalDateTime fechaReferencia = LocalDateTime.now();
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> usuarioPruebas.registrarIntentoFallido(
                        maxIntentosFallidosInvalidos, minutosDeBloqueo, fechaReferencia
                )
        );
        assertEquals("El máximo de intentos debe ser mayor a 0", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"0", "-1"})
    void deberiaLanzarExcepcionSiLosMinutosDeBloqueoSonInvalidosAlRegistrarIntentoFallido(
            int minutosDeBloqueoInvalidos
    ) {
        //ARRANGE
        int maxIntentosFallidos = 3;
        LocalDateTime fechaReferencia = LocalDateTime.now();
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> usuarioPruebas.registrarIntentoFallido(
                        maxIntentosFallidos, minutosDeBloqueoInvalidos, fechaReferencia
                )
        );
        assertEquals("Los minutos de bloqueo deben ser mayores a 0", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaFechaDeBloqueoEsNulaAlRegistrarIntentoFallido(){
        //ARRANGE
        int maxIntentosFallidos = 1;
        int minutosDeBloqueo = 10;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> usuarioPruebas.registrarIntentoFallido(
                        maxIntentosFallidos, minutosDeBloqueo, null
                )
        );
        assertEquals("La fecha de referencia no puede ser nula", exception.getMessage());
    }

    @Test
    void deberiaLimpiarIntentosFallidosYBloqueoCorrectamente(){
        //ARRANGE
        int maxIntentosFallidos = 1;
        int minutosDeBloqueo = 10;
        LocalDateTime fechaReferencia = LocalDateTime.now();
        usuarioPruebas.registrarIntentoFallido(maxIntentosFallidos, minutosDeBloqueo, fechaReferencia);
        //ACT
        usuarioPruebas.limpiarIntentosFallidosYBloqueo();
        //ASSERT
        assertEquals(0, usuarioPruebas.getIntentosFallidos());
        assertNull(usuarioPruebas.getBloqueadoHasta());
    }

    @Test
    void deberiaAgregarRolCorrectamente(){
        //ARRANGE
        rolPruebas.anadirPermiso(permisoPruebas);
        Set<String> rolesEsperados = rolPruebas.getPermisos().stream()
                .map(Permiso::getNombre)
                .collect(Collectors.toSet());
        //ACT
        usuarioPruebas.anadirRol(rolPruebas);
        //ASSERT
        assertEquals(rolesEsperados, usuarioPruebas.getPermisosCacheados());
        assertTrue(usuarioPruebas.getRoles().contains(rolPruebas));
    }

    @Test
    void deberiaQuitarRolCorrectamente(){
        //ARRANGE
        usuarioPruebas.anadirRol(rolPruebas);
        //ACT
        usuarioPruebas.quitarRol(rolPruebas);
        //ASSERT
        assertFalse(usuarioPruebas.getRoles().contains(rolPruebas));
    }

    @Test
    void deberiaAsignarContrasenaTemporal(){
        //ARRANGE
        int maxIntentosFallidos = 1;
        int minutosDeBloqueo = 10;
        LocalDateTime fechaReferencia = LocalDateTime.now();
        usuarioPruebas.registrarIntentoFallido(maxIntentosFallidos, minutosDeBloqueo, fechaReferencia);
        String hashContrasenaTemporal = "$argon2id$v=35$m=67580,t=1,p=2$c2FsdGdlbmVyYWRv$hashTemporalFalso...";
        //ACT
        usuarioPruebas.asignarContrasenaTemporal(hashContrasenaTemporal);
        //ASSERT
        assertEquals(hashContrasenaTemporal, usuarioPruebas.getHash());
        assertTrue(usuarioPruebas.isDebeCambiarContrasena());
        assertEquals(0, usuarioPruebas.getIntentosFallidos());
        assertNull(usuarioPruebas.getBloqueadoHasta());
    }

    @Test
    void deberiaEstablecerContrasenaDefinitivaCorrectamente(){
        //ARRANGE
        String hashContrasenaDefinitiva = "$argon3id$v=12$m=53189,t=4,p=3$c7FsdGdlbmVyYWRv$hashDefinitivoFalso...";
        //ACT
        usuarioPruebas.establecerContrasenaDefinitiva(hashContrasenaDefinitiva);
        //ASSERT
        assertEquals(hashContrasenaDefinitiva, usuarioPruebas.getHash());
        assertFalse(usuarioPruebas.isDebeCambiarContrasena());
    }

}//===================================================================================================================//

