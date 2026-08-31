package RetailManagementSystem.dominio.entidades.gestion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ImpuestoTest {

    private static final Integer ID_POR_DEFECTO = 1;
    private static final String NOMBRE_POR_DEFECTO = "Impuesto Estándar";
    private static final BigDecimal PORCENTAJE_POR_DEFECTO = new BigDecimal("19.00");
    private static final Boolean ACTIVO_POR_DEFECTO = true;

    private Impuesto impuestoMutador;

    @BeforeEach
    void setUp() {
        impuestoMutador = Impuesto.reconstruirDesdeBD(
                ID_POR_DEFECTO,
                NOMBRE_POR_DEFECTO,
                PORCENTAJE_POR_DEFECTO,
                ACTIVO_POR_DEFECTO
        );
    }

    @Test
    void deberiaCrearImpuestoCorrectamente(){
        //ACT
        Impuesto impuesto = Impuesto.crearNuevo(NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, ACTIVO_POR_DEFECTO);
        //ASSERT
        assertNull(impuesto.getId());
        assertEquals(NOMBRE_POR_DEFECTO, impuesto.getNombre());
        assertEquals(0, impuesto.getPorcentaje().compareTo(PORCENTAJE_POR_DEFECTO));
        assertEquals(ACTIVO_POR_DEFECTO, impuesto.isActivo());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si el nombre es nulo o esta vacío")
    @CsvSource( value = { "null", "''", "'    '" } , nullValues = "null" )
    void deberiaLanzarExcepcionSiElNombreEsInvalido(String nombre){
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Impuesto.crearNuevo(nombre, PORCENTAJE_POR_DEFECTO, ACTIVO_POR_DEFECTO)
        );
        //ASSERT
        assertEquals("El Nombre del Impuesto NO puede estar Vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElPorcentajeEsNulo(){
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Impuesto.crearNuevo(NOMBRE_POR_DEFECTO, null, ACTIVO_POR_DEFECTO)
        );
        //ASSERT
        assertEquals("El Porcentaje del Impuesto NO puede ser Nulo", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "0, 0.00", "33.33333, 33.33", "100, 100.00" })
    void deberiaPermitirPorcentajesValidosYLimites(String porcentajeEntrada, String porcentajeSalida){
        //ARRANGE
        BigDecimal entrada = new BigDecimal(porcentajeEntrada);
        BigDecimal esperado = new BigDecimal(porcentajeSalida);
        //ACT
        Impuesto impuesto = Impuesto.crearNuevo(NOMBRE_POR_DEFECTO, entrada, ACTIVO_POR_DEFECTO);
        //ASSERT
        assertNull(impuesto.getId());
        assertEquals(NOMBRE_POR_DEFECTO, impuesto.getNombre());
        assertEquals(0, impuesto.getPorcentaje().compareTo(esperado));
        assertEquals(ACTIVO_POR_DEFECTO, impuesto.isActivo());
    }

    @ParameterizedTest
    @CsvSource({ "-1", "101", "200" })
    void deberiaLanzarExcepcionSiElPorcentajeEsInvalido(String porcentajeSt){
        //ARRANGE
        BigDecimal porcentaje = new BigDecimal(porcentajeSt);
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Impuesto.crearNuevo(NOMBRE_POR_DEFECTO, porcentaje, ACTIVO_POR_DEFECTO)
        );
        //ASSERT
        assertEquals("Porcentaje de Impuesto Invalido:  " + porcentaje + "%", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElEstadoEsNulo(){
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Impuesto.crearNuevo(NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, null)
        );
        //ASSERT
        assertEquals("El Estado del Impuesto es Obligatorio", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si al cambiar el nombre es nulo o esta vacío")
    @CsvSource( value = { "null", "''", "'    '" } , nullValues = "null" )
    void deberiaLanzarExcepcionSiAlCambiarElNombreEsInvalido(String nombreNuevo){
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> impuestoMutador.cambiarNombre(nombreNuevo)
        );
        //ASSERT
        assertEquals("El Nombre del Impuesto NO puede estar Vacío", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "  Modificado  ", "Modificado"})
    void deberiaCambiarNombreCorrectamente(String nombreNuevo){
        //ACT
        impuestoMutador.cambiarNombre(nombreNuevo);
        //ASSERT
        assertEquals("Modificado", impuestoMutador.getNombre());
    }

    @ParameterizedTest
    @CsvSource({ "-1", "101", "200" })
    void deberiaLanzarExcepcionSiAlCambiarElPorcentajeEsInvalido(String porcentajeSt){
        //ARRANGE
        BigDecimal porcentajeNuevo = new BigDecimal(porcentajeSt);
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> impuestoMutador.cambiarPorcentaje(porcentajeNuevo)
        );
        //ASSERT
        assertEquals("Porcentaje de Impuesto Invalido:  " + porcentajeNuevo + "%", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarPorcentajeEsNulo(){
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> impuestoMutador.cambiarPorcentaje(null)
        );
        //ASSERT
        assertEquals("El Porcentaje del Impuesto NO puede ser Nulo", exception.getMessage());
    }

    @Test
    void deberiaCambiarEstadoCorrectamente(){
        //ACT
        impuestoMutador.cambiarEstado();
        //ASSERT
        assertFalse(impuestoMutador.isActivo());
    }

}

