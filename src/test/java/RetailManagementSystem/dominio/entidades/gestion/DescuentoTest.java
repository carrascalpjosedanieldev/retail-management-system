package RetailManagementSystem.dominio.entidades.gestion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class DescuentoTest {

    private static final Integer ID_POR_DEFECTO = 1;
    private static final String NOMBRE_POR_DEFECTO = "Descuento Estándar";
    private static final BigDecimal PORCENTAJE_POR_DEFECTO = new BigDecimal("15");
    private static final Boolean ACTIVO_POR_DEFECTO = true;

    private Descuento descuentoMutador;

    @BeforeEach
    void setUp() {
        descuentoMutador = Descuento.reconstruirDesdeBD(
                ID_POR_DEFECTO,
                NOMBRE_POR_DEFECTO,
                PORCENTAJE_POR_DEFECTO,
                ACTIVO_POR_DEFECTO
        );
    }

    @Test
    void deberiaCrearDescuentoCorrectamente(){
        Descuento descuento = Descuento.crearNuevo(NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, ACTIVO_POR_DEFECTO);
        assertNull(descuento.getId());
        assertEquals(NOMBRE_POR_DEFECTO.trim(), descuento.getNombre());
        assertEquals(0, descuento.getPorcentaje().compareTo(PORCENTAJE_POR_DEFECTO));
        assertEquals(ACTIVO_POR_DEFECTO, descuento.isActivo());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si el nombre es nulo o esta vacío")
    @CsvSource( value = { "null", "''", "'    '" } , nullValues = "null" )
    void deberiaLanzarExcepcionSiElNombreEsInvalido(String nombre){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Descuento.crearNuevo(nombre, PORCENTAJE_POR_DEFECTO, ACTIVO_POR_DEFECTO)
        );
        assertEquals("El Nombre del Descuento NO puede estar Vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElPorcentajeEsNulo(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Descuento.crearNuevo(NOMBRE_POR_DEFECTO, null, ACTIVO_POR_DEFECTO)
        );
        assertEquals("El Porcentaje del Descuento NO puede ser Nulo", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "0", "50", "100" })
    void deberiaPermitirPorcentajesValidosYLimites(String porcentajeSt){
        BigDecimal porcentaje = new BigDecimal(porcentajeSt);
        Descuento descuento = Descuento.crearNuevo(NOMBRE_POR_DEFECTO, porcentaje, ACTIVO_POR_DEFECTO);
        assertNull(descuento.getId());
        assertEquals(NOMBRE_POR_DEFECTO.trim(), descuento.getNombre());
        assertEquals(0, descuento.getPorcentaje().compareTo(porcentaje));
        assertEquals(ACTIVO_POR_DEFECTO, descuento.isActivo());
    }

    @ParameterizedTest
    @CsvSource({ "-1", "101", "200" })
    void deberiaLanzarExcepcionSiElPorcentajeEsInvalido(String porcentajeSt){
        BigDecimal porcentaje = new BigDecimal(porcentajeSt);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Descuento.crearNuevo(NOMBRE_POR_DEFECTO, porcentaje, ACTIVO_POR_DEFECTO)
        );
        assertEquals("Porcentaje de Descuento Invalido:  " + porcentaje + "%", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElEstadoEsNulo(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Descuento.crearNuevo(NOMBRE_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, null)
        );
        assertEquals("El Estado del Descuento es Obligatorio", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si al cambiar el nombre es nulo o esta vacío")
    @CsvSource( value = { "null", "''", "'    '" } , nullValues = "null" )
    void deberiaLanzarExcepcionSiAlCambiarElNombreEsInvalido(String nombreNuevo){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> descuentoMutador.cambiarNombre(nombreNuevo)
        );
        assertEquals("El Nombre del Descuento NO puede estar Vacío", exception.getMessage());
    }

    @Test
    void deberiaCambiarNombreCorrectamente(){
        String nombreNuevo = "Descuento Modificado";
        descuentoMutador.cambiarNombre(nombreNuevo);
        assertEquals(nombreNuevo.trim(), descuentoMutador.getNombre());
    }

    @ParameterizedTest
    @CsvSource({ "-1", "101", "200" })
    void deberiaLanzarExcepcionSiAlCambiarElPorcentajeEsInvalido(String porcentajeSt){
        BigDecimal porcentajeNuevo = new BigDecimal(porcentajeSt);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> descuentoMutador.cambiarPorcentaje(porcentajeNuevo)
        );
        assertEquals("Porcentaje de Descuento Invalido:  " + porcentajeNuevo + "%", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarPorcentajeEsNulo(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> descuentoMutador.cambiarPorcentaje(null)
        );
        assertEquals("El Porcentaje del Descuento NO puede ser Nulo", exception.getMessage());
    }

}

