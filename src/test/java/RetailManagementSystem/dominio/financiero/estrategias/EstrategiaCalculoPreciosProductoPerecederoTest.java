package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstrategiaCalculoPreciosProductoPerecederoTest extends EstrategiaCalculoPreciosProductoBaseAbstractaTest<ProductoPerecedero> {

    @InjectMocks
    private EstrategiaCalculoPreciosProductoPerecedero estrategiaProductoPerecedero;

    //TESTS

    @Override
    protected EstrategiaCalculoPreciosProductoBase<ProductoPerecedero> instanciarEstrategia(MatematicaFinanciera matematicaFinanciera) {
        return new EstrategiaCalculoPreciosProductoPerecedero(matematicaFinanciera);
    }

    @Override
    protected ProductoPerecedero mockearProducto() {
        return Mockito.mock(ProductoPerecedero.class);
    }

    @Test
    @Override
    void deberiaCalcularValorFinalSinImpuestoCorrectamente() {
        //ARRANGE
        ProductoPerecedero perecedero = mock(ProductoPerecedero.class);
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        LocalDate fechaActual = LocalDate.now();
        BigDecimal precioBase = new BigDecimal("5000");
        when(perecedero.getPrecioBase()).thenReturn(precioBase);
        when(contexto.getFechaEvaluacion()).thenReturn(Optional.of(fechaActual));
        BigDecimal descuentoPolitica = new BigDecimal("250");
        when(perecedero.calcularDescuentoPolitica(precioBase, fechaActual))
                .thenReturn(descuentoPolitica);
        Descuento descuento = mock(Descuento.class);
        when(perecedero.getDescuento()).thenReturn(descuento);
        BigDecimal montoDescuento = new BigDecimal("500");
        when(matematicaFinancieraFalso.calcularMontoDescuento(precioBase, descuento)).thenReturn(montoDescuento);
        BigDecimal valorFinalSinImpuesto = new BigDecimal("4250");
        BigDecimal valorFinalSinImpuestoConEscala = new BigDecimal("4250.000000");
        when(matematicaFinancieraFalso.aplicarEscala(valorFinalSinImpuesto)).thenReturn(valorFinalSinImpuestoConEscala);
        //ACT
        BigDecimal resultado = estrategiaProductoPerecedero.calcularValorFinalSinImpuesto(perecedero, contexto);
        //ASSERT
        assertEquals(valorFinalSinImpuestoConEscala, resultado);
        verify(matematicaFinancieraFalso).calcularMontoDescuento(precioBase, descuento);
        verify(matematicaFinancieraFalso).aplicarEscala(valorFinalSinImpuesto);
        verifyNoMoreInteractions(matematicaFinancieraFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiFechaDeEvaluacionEstaVaciaAlCalcularSinImpuesto() {
        // ARRANGE
        ProductoPerecedero perecedero = mock(ProductoPerecedero.class);
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        when(perecedero.getPrecioBase()).thenReturn(new BigDecimal("10000"));
        when(contexto.getFechaEvaluacion()).thenReturn(Optional.empty());
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estrategia.calcularValorFinalSinImpuesto(perecedero, contexto)
        );
        assertEquals("Se Requiere una Fecha para Calcular el Valor del Producto", exception.getMessage());
        verifyNoInteractions(matematicaFinancieraFalso);
    }

    @Test
    @Override
    void deberiaCalcularValorVentaCorrectamente() {
        // ARRANGE
        ProductoPerecedero perecedero = mock(ProductoPerecedero.class);
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        Descuento descuento = mock(Descuento.class);
        Impuesto impuesto = mock(Impuesto.class);
        LocalDate fechaActual = LocalDate.now();
        BigDecimal precioBase = new BigDecimal("100000");
        when(perecedero.getPrecioBase()).thenReturn(precioBase);
        when(perecedero.getDescuento()).thenReturn(descuento);
        when(perecedero.getImpuesto()).thenReturn(impuesto);
        when(contexto.getFechaEvaluacion()).thenReturn(Optional.of(fechaActual));
        BigDecimal descuentoPolitica = new BigDecimal("20000");
        when(perecedero.calcularDescuentoPolitica(precioBase, fechaActual)).thenReturn(descuentoPolitica);
        BigDecimal montoDescuentoNormal = new BigDecimal("10000");
        when(matematicaFinancieraFalso.calcularMontoDescuento(precioBase, descuento)).thenReturn(montoDescuentoNormal);
        BigDecimal valorSinImpuestoPuro = new BigDecimal("70000");
        BigDecimal valorSinImpuestoConEscala = new BigDecimal("70000.000000");
        when(matematicaFinancieraFalso.aplicarEscala(valorSinImpuestoPuro)).thenReturn(valorSinImpuestoConEscala);
        BigDecimal montoImpuesto = new BigDecimal("13300.000000");
        when(matematicaFinancieraFalso.calcularMontoImpuesto(valorSinImpuestoConEscala, impuesto)).thenReturn(montoImpuesto);
        BigDecimal valorVentaPuro = new BigDecimal("83300.000000");
        BigDecimal valorVentaConEscala = new BigDecimal("83300.000000");
        when(matematicaFinancieraFalso.aplicarEscala(valorVentaPuro)).thenReturn(valorVentaConEscala);
        // ACT
        BigDecimal resultado = estrategia.calcularValorVenta(perecedero, contexto);
        // ASSERT
        assertEquals(valorVentaConEscala, resultado);
        verify(matematicaFinancieraFalso).calcularMontoDescuento(precioBase, descuento);
        verify(matematicaFinancieraFalso).aplicarEscala(valorSinImpuestoPuro);
        verify(matematicaFinancieraFalso).calcularMontoImpuesto(valorSinImpuestoConEscala, impuesto);
        verify(matematicaFinancieraFalso).aplicarEscala(valorVentaPuro);
        verifyNoMoreInteractions(matematicaFinancieraFalso);
    }

}//===================================================================================================================//

