package RetailManagementSystem.dominio.entidades.seguridad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class PermisoTest {

    private Permiso permisoPrueba;

    @BeforeEach
    void setUp() {
        permisoPrueba = Permiso.reconstruirDesdeBD(
                1,
                "Crear usuarios",
                "Permite crear nuevos usuarios en el sistema",
                "Seguridad",
                true
        );
    }

    //TESTs

    @Test
    void deberiaReconstruirDesdeBDCorrectamenteYFormatearNombre() {
        // ACT
        Permiso permiso = Permiso.reconstruirDesdeBD(
                2,
                "  Editar reportes  ",
                "Permite editar reportes financieros",
                "Reportes",
                false
        );
        // ASSERT
        assertEquals(2, permiso.getIdPermiso());
        assertEquals("EDITAR REPORTES", permiso.getNombre());
        assertEquals("Permite editar reportes financieros", permiso.getDescripcion());
        assertEquals("Reportes", permiso.getModulo());
        assertFalse(permiso.isActivo());
    }

    @ParameterizedTest
    @CsvSource( value = {"null", "'   '", "''"} , nullValues = "null" )
    void deberiaLanzarExcepcionSiNombreEsInvalido(String nombreInvalido) {
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Permiso.reconstruirDesdeBD(
                        1, nombreInvalido, "Descripción", "Módulo", true
                )
        );
        assertEquals("Nombre del Permiso Vacío", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource( value = {"null", "'   '", "''"} , nullValues = "null" )
    void deberiaLanzarExcepcionSiDescripcionEsInvalida(String descripcionInvalida) {
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Permiso.reconstruirDesdeBD(
                        1, "NOMBRE", descripcionInvalida, "Módulo", true
                )
        );
        assertEquals("Descripción del Permiso Nula", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource( value = {"null", "'   '", "''"} , nullValues = "null" )
    void deberiaLanzarExcepcionSiModuloEsInvalido(String moduloInvalido) {
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Permiso.reconstruirDesdeBD(1, "NOMBRE", "Descripción", moduloInvalido, true)
        );
        assertEquals("Nombre del Modulo del Permiso Vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiEstadoActivoEsNulo() {
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Permiso.reconstruirDesdeBD(
                        1, "NOMBRE", "Descripción", "Módulo", null
                )
        );
        assertEquals("El Estado del Permiso es Obligatorio", exception.getMessage());
    }

    @Test
    void deberiaCambiarDescripcionCorrectamente() {
        // ARRANGE
        String nuevaDescripcion = "Nueva descripción actualizada";
        // ACT
        permisoPrueba.cambiarDescripcion(nuevaDescripcion);
        // ASSERT
        assertEquals(nuevaDescripcion, permisoPrueba.getDescripcion());
    }

    @ParameterizedTest
    @CsvSource( value = {"null", "'   '", "''"} , nullValues = "null" )
    void deberiaLanzarExcepcionAlCambiarPorDescripcionInvalida(String descripcionInvalida) {
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> permisoPrueba.cambiarDescripcion(descripcionInvalida)
        );
        assertEquals("Descripción del Permiso Nula", exception.getMessage());
    }

    @Test
    void deberiaAlternarEstadoCorrectamente() {
        // ACT & ASSERT
        permisoPrueba.cambiarEstado();
        assertFalse(permisoPrueba.isActivo());
        // ACT & ASSERT
        permisoPrueba.cambiarEstado();
        assertTrue(permisoPrueba.isActivo());
    }

    @Test
    void deberiaSerIgualAOtroPermisoConMismoNombreIgnorandoMayusculas() {
        // ARRANGE
        Permiso permisoMismoNombre = Permiso.reconstruirDesdeBD(
                99,
                "CREAR USUARIOS",
                "Otra descripción diferente",
                "Otro módulo",
                false
        );
        // ACT & ASSERT
        assertEquals(permisoPrueba, permisoMismoNombre);
        assertEquals(permisoPrueba.hashCode(), permisoMismoNombre.hashCode());
    }

    @Test
    void noDeberiaSerIgualAUnPermisoConNombreDiferente() {
        // ARRANGE
        Permiso permisoDiferente = Permiso.reconstruirDesdeBD(
                1,
                "ELIMINAR USUARIOS",
                "Permite crear nuevos usuarios en el sistema",
                "Seguridad",
                true
        );
        // ACT & ASSERT
        assertNotEquals(permisoPrueba, permisoDiferente);
        assertNotEquals(permisoPrueba.hashCode(), permisoDiferente.hashCode());
    }

}//===================================================================================================================//

