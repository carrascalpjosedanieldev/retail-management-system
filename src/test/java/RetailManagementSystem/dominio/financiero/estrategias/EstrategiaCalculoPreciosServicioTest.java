package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstrategiaCalculoPreciosServicioTest {

    @Mock
    private MatematicaFinanciera matematicaFinancieraFalso;

    @InjectMocks
    private EstrategiaCalculoPreciosServicio estrategiaCalculoPreciosServicio;

    //TESTS

    @Test
    void deberiaCalcularValorFinalSinImpuestoCorrectamente(){
        //ARRANGE
        Servicio servicio = mock(Servicio.class);
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        BigDecimal precioBase = new BigDecimal("15000");
        when(servicio.getPrecioBase()).thenReturn(precioBase);
        Descuento descuento = mock(Descuento.class);
        when(servicio.getDescuento()).thenReturn(descuento);
        BigDecimal montoDescuento = new BigDecimal("1000");
        when(matematicaFinancieraFalso.calcularMontoDescuento(precioBase, descuento)).thenReturn(montoDescuento);
        BigDecimal valorFinalSinImpuestoSinEscala = new BigDecimal("14000");
        BigDecimal valorFinalSinImpuestoEsperado = new BigDecimal("14000.000000");
        when(matematicaFinancieraFalso.aplicarEscala(valorFinalSinImpuestoSinEscala)).thenReturn(valorFinalSinImpuestoEsperado);
        //ACT
        BigDecimal resultado = estrategiaCalculoPreciosServicio.calcularValorFinalSinImpuesto(servicio, contexto);
        //ASSERT
        assertEquals(valorFinalSinImpuestoEsperado, resultado);
        verify(matematicaFinancieraFalso).calcularMontoDescuento(precioBase, descuento);
        verify(matematicaFinancieraFalso).aplicarEscala(valorFinalSinImpuestoSinEscala);
        verifyNoMoreInteractions(matematicaFinancieraFalso);
    }

    @Test
    void deberiaCalcularValorVentaCorrectamente(){
        //ARRANGE
        Servicio servicio = mock(Servicio.class);
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        BigDecimal precioBase = new BigDecimal("15000");
        when(servicio.getPrecioBase()).thenReturn(precioBase);
        Descuento descuento = mock(Descuento.class);
        when(servicio.getDescuento()).thenReturn(descuento);
        BigDecimal montoDescuento = new BigDecimal("1000");
        when(matematicaFinancieraFalso.calcularMontoDescuento(precioBase, descuento)).thenReturn(montoDescuento);
        BigDecimal valorFinalSinImpuestoSinEscala = new BigDecimal("14000");
        BigDecimal valorFinalSinImpuesto = new BigDecimal("14000.000000");
        when(matematicaFinancieraFalso.aplicarEscala(valorFinalSinImpuestoSinEscala)).thenReturn(valorFinalSinImpuesto);
        Impuesto impuesto = mock(Impuesto.class);
        when(servicio.getImpuesto()).thenReturn(impuesto);
        BigDecimal montoImpuesto = new BigDecimal("1000");
        when(matematicaFinancieraFalso.calcularMontoImpuesto(valorFinalSinImpuesto, impuesto))
                .thenReturn(montoImpuesto);
        BigDecimal valorVenta = new BigDecimal("15000.000000");
        BigDecimal valorVentaEsperado = new BigDecimal("15000.000000");
        when(matematicaFinancieraFalso.aplicarEscala(valorVenta)).thenReturn(valorVentaEsperado);
        //ACT
        BigDecimal resultado = estrategiaCalculoPreciosServicio.calcularValorVenta(servicio, contexto);
        //ASSERT
        assertEquals(valorVentaEsperado, resultado);
        verify(matematicaFinancieraFalso).calcularMontoDescuento(precioBase, descuento);
        verify(matematicaFinancieraFalso).aplicarEscala(valorFinalSinImpuestoSinEscala);
        verify(matematicaFinancieraFalso).calcularMontoImpuesto(valorFinalSinImpuesto, impuesto);
        verify(matematicaFinancieraFalso).aplicarEscala(valorVenta);
        verifyNoMoreInteractions(matematicaFinancieraFalso);
    }

}//===================================================================================================================//

