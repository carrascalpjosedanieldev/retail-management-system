package RetailManagementSystem.dominio.entidades.seguridad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RolTest {

    private static final String NOMBRE_POR_DEFECTO = "Rol nuevo";

    private final Permiso permisoPrueba = Permiso.reconstruirDesdeBD(
            1,
            "Permiso prueba",
            "Descripción normal",
            "Nombre modulo",
            true
    );

    private Rol rolPrueba;

    @BeforeEach
    void setUp(){
        rolPrueba = Rol.crearNuevo(
                NOMBRE_POR_DEFECTO,
                true
        );
    }

    //TESTS

    @Test
    void deberiaReconstruirDedeBDCorrectamente(){
        //ARRANGE
        int idRol = 1;
        String nombre = "Rol recuperado";
        //ACT
        Rol rol = Rol.reconstruirDesdeBD(
                idRol, nombre, true
        );
        //ASSERT
        assertEquals(idRol, rol.getIdRol());
        assertEquals(nombre, rol.getNombre());
        assertEquals(Set.of(), rol.getPermisos());
        assertTrue(rol.isActivo());
    }

    @ParameterizedTest
    @CsvSource( value = {"'   '", "''", "null"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElNombreEsInvalidoAlReconstruirDesdeBD(String nombreInvalido){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Rol.reconstruirDesdeBD(1, nombreInvalido, true)
        );
        assertEquals("Nombre del Rol Vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElEstadoEsNuloAlReconstruirDesdeBD(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Rol.reconstruirDesdeBD(1, NOMBRE_POR_DEFECTO, null)
        );
        assertEquals("El Estado del Rol es Obligatorio", exception.getMessage());
    }

    @Test
    void deberiaCrearNuevoCorrectamente(){
        //ARRANGE
        int idRol = 1;
        String nombre = "  Rol nuevo  ";
        //ACT
        Rol rol = Rol.reconstruirDesdeBD(
                idRol, nombre, true
        );
        //ASSERT
        assertEquals(idRol, rol.getIdRol());
        assertEquals("Rol nuevo", rol.getNombre());
        assertEquals(Set.of(), rol.getPermisos());
        assertTrue(rol.isActivo());
    }

    @ParameterizedTest
    @CsvSource( value = {"'   '", "''", "null"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElNombreEsInvalidoAlCrearNuevo(String nombreInvalido){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Rol.crearNuevo(nombreInvalido, true)
        );
        assertEquals("Nombre del Rol Vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElEstadoEsNuloAlCrearNuevo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Rol.crearNuevo(NOMBRE_POR_DEFECTO, null)
        );
        assertEquals("El Estado del Rol es Obligatorio", exception.getMessage());
    }

    @Test
    void deberiaCambiarNombreCorrectamente(){
        //ARRANGE
        String nombreNuevo = "   Nombre nuevo   ";
        //ACT
        rolPrueba.cambiarNombre(nombreNuevo);
        //ASSERT
        assertEquals("Nombre nuevo", rolPrueba.getNombre());
    }

    @Test
    void deberiaCambiarEstadoCorrectamente(){
        //ARRANGE
        boolean estadoAnterior = rolPrueba.isActivo();
        //ACT
        rolPrueba.cambiarEstado();
        //ASSERT
        assertEquals(!estadoAnterior, rolPrueba.isActivo());
    }

    @Test
    void deberiaAgregarPermisoCorrectamente(){
        //ACT
        rolPrueba.anadirPermiso(permisoPrueba);
        //ASSERT
        assertTrue(rolPrueba.getPermisos().contains(permisoPrueba));
    }

    @Test
    void deberiaEliminarPermisoCorrectamente(){
        //ARRANGE
        rolPrueba.anadirPermiso(permisoPrueba);
        //ACT
        rolPrueba.quitarPermiso(permisoPrueba);
        //ASSERT
        assertFalse(rolPrueba.getPermisos().contains(permisoPrueba));
    }

    @Test
    void deberiaTomarComoIgualesDosRolesPorSuIDRol(){
        //ARRANGE
        Rol rol1 = Rol.reconstruirDesdeBD(1, "Nombre uno", true);
        Rol rol2 = Rol.reconstruirDesdeBD(1, "Nombre diferente", false);
        //ACT AND ASSERT
        assertEquals(rol1, rol2);
        assertEquals(rol1.hashCode(), rol2.hashCode());
    }

}//===================================================================================================================//

