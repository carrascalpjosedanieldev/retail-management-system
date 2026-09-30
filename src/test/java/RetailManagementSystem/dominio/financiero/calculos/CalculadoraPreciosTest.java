package RetailManagementSystem.dominio.financiero.calculos;

import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.financiero.estrategias.EstrategiaCalculoPrecios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculadoraPreciosTest {

    @Mock
    private MatematicaFinanciera matematicaFinancieraFalsa;

    @Mock
    private EstrategiaCalculoPrecios<ProductoRopa> estrategiaRopaFalsa;

    private CalculadoraPrecios calculadoraPrecios;

    @BeforeEach
    void setUp() {
        Map<Class<? extends ItemFacturable>, EstrategiaCalculoPrecios<?>> mapaEstrategias = new HashMap<>();
        mapaEstrategias.put(ProductoRopa.class, estrategiaRopaFalsa);
        calculadoraPrecios = new CalculadoraPrecios(matematicaFinancieraFalsa, mapaEstrategias);
    }

    //TESTS

    @Test
    void deberiaLanzarExcepcionSiNoExisteEstrategiaAlCalcularValorVenta(){
        //ARRANGE
        ProductoPerecedero itemSinEstrategia = mock(ProductoPerecedero.class);
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        //ACT & ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> calculadoraPrecios.calcularValorVenta(itemSinEstrategia, contexto)
        );
        assertTrue(exception.getMessage().contains("NO Existe una Estrategia de Cálculo Registrada para la Clase"));
        verifyNoInteractions(matematicaFinancieraFalsa);
    }

    @Test
    void deberiaLanzarExcepcionSiNoExisteEstrategiaAlCalcularValorFinalSinImpuesto(){
        //ARRANGE
        ProductoPerecedero itemSinEstrategia = mock(ProductoPerecedero.class);
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        //ACT & ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> calculadoraPrecios.calcularValorFinalSinImpuesto(itemSinEstrategia, contexto)
        );
        assertTrue(exception.getMessage().contains("NO Existe una Estrategia de Cálculo Registrada para la Clase"));
        verifyNoInteractions(matematicaFinancieraFalsa);
    }

    @Test
    void deberiaDelegarElCalculoDelValorDeVentaALaEstrategiaCorrecta(){
        //ARRANGE
        ProductoRopa ropa = mock(ProductoRopa.class);
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        BigDecimal valorEsperado = new BigDecimal("150000.00");
        when(estrategiaRopaFalsa.calcularValorVenta(ropa, contexto)).thenReturn(valorEsperado);
        //ACT
        BigDecimal resultado = calculadoraPrecios.calcularValorVenta(ropa, contexto);
        //ASSERT
        assertEquals(valorEsperado, resultado);
        verify(estrategiaRopaFalsa).calcularValorVenta(ropa, contexto);
        verifyNoInteractions(matematicaFinancieraFalsa);
    }

    @Test
    void deberiaDelegarElCalculoDelValorFinalSinImpuestoALaEstrategiaCorrecta(){
        //ARRANGE
        ProductoRopa ropa = mock(ProductoRopa.class);
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        BigDecimal valorEsperado = new BigDecimal("12000.00");
        when(estrategiaRopaFalsa.calcularValorFinalSinImpuesto(ropa, contexto)).thenReturn(valorEsperado);
        //ACT
        BigDecimal resultado = calculadoraPrecios.calcularValorFinalSinImpuesto(ropa, contexto);
        //ASSERT
        assertEquals(valorEsperado, resultado);
        verify(estrategiaRopaFalsa).calcularValorFinalSinImpuesto(ropa, contexto);
        verifyNoInteractions(matematicaFinancieraFalsa);
    }

    @Test
    void deberiaDelegarElCalculoDelImpuestoAMatematicaFinanciera(){
        //ARRANGE
        BigDecimal valorFinalSinImpuesto = new BigDecimal("10000.00");
        Impuesto impuesto = mock(Impuesto.class);
        BigDecimal valorEsperado = new BigDecimal("1900.00");
        when(matematicaFinancieraFalsa.calcularMontoImpuesto(valorFinalSinImpuesto, impuesto))
                .thenReturn(valorEsperado);
        //ACT
        BigDecimal resultado = calculadoraPrecios.calcularImpuesto(valorFinalSinImpuesto, impuesto);
        //ASSERT
        assertEquals(resultado, valorEsperado);
        verify(matematicaFinancieraFalsa).calcularMontoImpuesto(valorFinalSinImpuesto, impuesto);
        verifyNoInteractions(estrategiaRopaFalsa);
    }

}//===================================================================================================================//

