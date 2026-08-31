package RetailManagementSystem.dominio.entidades.gestion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PoliticaVencimientoTest {

    private static final Integer ID_POR_DEFECTO = 1;
    private static final String NOMBRE_POR_DEFECTO = "Política General";
    private static final Integer DIAS_UMBRAL_POR_DEFECTO = 3;
    private static final BigDecimal PORCENTAJE_POR_DEFECTO = new BigDecimal("30.00");
    private static final Boolean ACTIVO_POR_DEFECTO = true;

    private PoliticaVencimiento politicaVMutadora;

    @BeforeEach
    void setUp(){
        politicaVMutadora = PoliticaVencimiento.reconstruirDesdeBD(
                ID_POR_DEFECTO,
                NOMBRE_POR_DEFECTO,
                DIAS_UMBRAL_POR_DEFECTO,
                PORCENTAJE_POR_DEFECTO,
                ACTIVO_POR_DEFECTO
        );
    }

    @Test
    void deberiaCrearPoliticaVCorrectamente(){
        //ACT
        PoliticaVencimiento politicaV = PoliticaVencimiento.crearNuevo(
                NOMBRE_POR_DEFECTO,
                DIAS_UMBRAL_POR_DEFECTO,
                PORCENTAJE_POR_DEFECTO,
                ACTIVO_POR_DEFECTO
        );
        //ASSERT
        assertNull(politicaV.getIdPolitica());
        assertEquals(NOMBRE_POR_DEFECTO, politicaV.getNombre());
        assertEquals(DIAS_UMBRAL_POR_DEFECTO, politicaV.getDiasUmbral());
        assertEquals(0, politicaV.getPorcentajeDescuento().compareTo(PORCENTAJE_POR_DEFECTO));
        assertEquals(ACTIVO_POR_DEFECTO, politicaV.isActiva());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si al crear nuevo el nombre es nulo o esta vacío")
    @CsvSource( value = { "null", "''", "'    '" } , nullValues = "null" )
    void deberiaLanzarExcepcionSiElNombreEsInvalido(String nombre){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> PoliticaVencimiento.crearNuevo(nombre, DIAS_UMBRAL_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, ACTIVO_POR_DEFECTO)
        );
        assertEquals("El Nombre de la Política de Vencimiento NO puede estar Vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar excepción si al crear nuevo el porcentaje es nulo")
    void deberiaLanzarExcepcionSiElPorcentajeEsNulo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> PoliticaVencimiento.crearNuevo(NOMBRE_POR_DEFECTO, DIAS_UMBRAL_POR_DEFECTO, null, ACTIVO_POR_DEFECTO)
        );
        assertEquals("El Porcentaje de la Política de Vencimiento NO puede ser Nulo", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debería permitir porcentajes validos y limites al crear nuevo")
    @CsvSource({ "0, 0.00", "33.33333, 33.33", "100, 100.00" })
    void deberiaPermitirPorcentajesValidosYLimites(String porcentajeEntrada, String porcentajeSalida){
        //ARRANGE
        BigDecimal entrada = new BigDecimal(porcentajeEntrada);
        BigDecimal esperado = new BigDecimal(porcentajeSalida);
        //ACT
        PoliticaVencimiento politicaV = PoliticaVencimiento.crearNuevo(
                NOMBRE_POR_DEFECTO, DIAS_UMBRAL_POR_DEFECTO, entrada, ACTIVO_POR_DEFECTO
        );
        assertNull(politicaV.getIdPolitica());
        assertEquals(NOMBRE_POR_DEFECTO, politicaV.getNombre());
        assertEquals(DIAS_UMBRAL_POR_DEFECTO, politicaV.getDiasUmbral());
        assertEquals(0, politicaV.getPorcentajeDescuento().compareTo(esperado));
        assertEquals(ACTIVO_POR_DEFECTO, politicaV.isActiva());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si al crear nuevo el porcentaje es invalido")
    @CsvSource({ "-1", "101", "200" })
    void deberiaLanzarExcepcionSiElPorcentajeEsInvalido(String porcentajeSt){
        //ARRANGE
        BigDecimal porcentaje = new BigDecimal(porcentajeSt);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> PoliticaVencimiento.crearNuevo(NOMBRE_POR_DEFECTO, DIAS_UMBRAL_POR_DEFECTO, porcentaje, ACTIVO_POR_DEFECTO)
        );
        assertEquals("Porcentaje de Descuento de Política de Vencimiento Invalido:  " + porcentaje + "%",
                exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si al crear nuevo los días umbral son nulos o inválidos")
    @CsvSource( value = { "-1", "null" } , nullValues = "null")
    void deberiaLanzarExcepcionSiLosDiasUmbralSonNulosOInvalidos(Integer diasUmbral){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> PoliticaVencimiento.crearNuevo(NOMBRE_POR_DEFECTO, diasUmbral, PORCENTAJE_POR_DEFECTO, ACTIVO_POR_DEFECTO)
        );
        assertEquals("Dias Umbral de la Política de Vencimiento Inválidos", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCrearNuevoElEstadoEsNulo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> PoliticaVencimiento.crearNuevo(NOMBRE_POR_DEFECTO, DIAS_UMBRAL_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, null)
        );
        assertEquals("El Estado de la Política de Vencimiento es Obligatorio", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si al cambiar el nombre es nulo o esta vacío")
    @CsvSource( value = { "null", "''", "'    '" } , nullValues = "null" )
    void deberiaLanzarExcepcionSiAlCambiarElNombreEsInvalido(String nombreNuevo){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> politicaVMutadora.cambiarNombrePolitica(nombreNuevo)
        );
        assertEquals("El Nombre de la Política de Vencimiento NO puede estar Vacío", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "  Modificado  ", "Modificado"})
    void deberiaCambiarNombreCorrectamente(String nombreNuevo){
        //ACT
        politicaVMutadora.cambiarNombrePolitica(nombreNuevo);
        //ASSERT
        assertEquals("Modificado", politicaVMutadora.getNombre());
    }

    @ParameterizedTest
    @CsvSource( value = { "-1", "null" } , nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarLosDiasUmbralEsNuloOInvalido(Integer diasUmbral){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> politicaVMutadora.cambiarDiasUmbral(diasUmbral)
        );
        assertEquals("Dias Umbral de la Política de Vencimiento Inválidos", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"0", "1", "5"})
    void deberiaCambiarDiasUmbralCorrectamente(Integer diasUmbral){
        //ACT
        politicaVMutadora.cambiarDiasUmbral(diasUmbral);
        //ASSERT
        assertEquals(diasUmbral, politicaVMutadora.getDiasUmbral());
    }

    @ParameterizedTest
    @CsvSource({ "-1", "101", "200" })
    void deberiaLanzarExcepcionSiAlCambiarElPorcentajeEsInvalido(String porcentajeSt){
        //ARRANGE
        BigDecimal porcentajeNuevo = new BigDecimal(porcentajeSt);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> politicaVMutadora.cambiarPorcentajeDescuento(porcentajeNuevo)
        );
        assertEquals("Porcentaje de Descuento de Política de Vencimiento Invalido:  " + porcentajeNuevo + "%",
                exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarPorcentajeEsNulo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> politicaVMutadora.cambiarPorcentajeDescuento(null)
        );
        assertEquals("El Porcentaje de la Política de Vencimiento NO puede ser Nulo", exception.getMessage());
    }

    @Test
    void deberiaCambiarEstadoCorrectamente(){
        //ACT
        politicaVMutadora.cambiarEstado();
        //ASSERT
        assertFalse(politicaVMutadora.isActiva());
    }

}

