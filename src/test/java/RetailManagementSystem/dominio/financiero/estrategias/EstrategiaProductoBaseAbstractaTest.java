package RetailManagementSystem.dominio.financiero.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.dominio.financiero.calculos.MatematicaFinanciera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public abstract class EstrategiaProductoBaseAbstractaTest<T extends Producto> {

    @Mock
    protected MatematicaFinanciera matematicaFinancieraFalso;

    protected EstrategiaProductoBase<T> estrategia;

    protected abstract EstrategiaProductoBase<T> instanciarEstrategia(MatematicaFinanciera matematicaFinanciera);

    protected abstract T mockearProducto();

    @BeforeEach
    void setUp() {
        this.estrategia = instanciarEstrategia(matematicaFinancieraFalso);
    }

    //TESTS

    @Test
    void deberiaCalcularValorFinalSinImpuestoCorrectamente() {
        // ARRANGE
        T producto = mockearProducto();
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        Descuento descuento = mock(Descuento.class);
        BigDecimal precioBase = new BigDecimal("100000");
        when(producto.getPrecioBase()).thenReturn(precioBase);
        when(producto.getDescuento()).thenReturn(descuento);
        BigDecimal montoDescuento = new BigDecimal("15000");
        when(matematicaFinancieraFalso.calcularMontoDescuento(precioBase, descuento)).thenReturn(montoDescuento);
        BigDecimal valorSinImpuesto = new BigDecimal("85000");
        BigDecimal valorEsperadoConEscala = new BigDecimal("85000.000000");
        when(matematicaFinancieraFalso.aplicarEscala(valorSinImpuesto)).thenReturn(valorEsperadoConEscala);
        // ACT
        BigDecimal resultado = estrategia.calcularValorFinalSinImpuesto(producto, contexto);
        // ASSERT
        assertEquals(valorEsperadoConEscala, resultado);
        verify(matematicaFinancieraFalso).calcularMontoDescuento(precioBase, descuento);
        verify(matematicaFinancieraFalso).aplicarEscala(valorSinImpuesto);
        verifyNoMoreInteractions(matematicaFinancieraFalso);
    }

    @Test
    void deberiaCalcularValorVentaCorrectamente() {
        // ARRANGE
        T producto = mockearProducto();
        ContextoEvaluacion contexto = mock(ContextoEvaluacion.class);
        Descuento descuento = mock(Descuento.class);
        Impuesto impuesto = mock(Impuesto.class);
        BigDecimal precioBase = new BigDecimal("100000");
        when(producto.getPrecioBase()).thenReturn(precioBase);
        when(producto.getDescuento()).thenReturn(descuento);
        when(producto.getImpuesto()).thenReturn(impuesto);
        BigDecimal montoDescuento = new BigDecimal("15000");
        when(matematicaFinancieraFalso.calcularMontoDescuento(precioBase, descuento)).thenReturn(montoDescuento);
        BigDecimal valorSinImpuestoPuro = new BigDecimal("85000");
        BigDecimal valorSinImpuestoConEscala = new BigDecimal("85000.000000");
        when(matematicaFinancieraFalso.aplicarEscala(valorSinImpuestoPuro)).thenReturn(valorSinImpuestoConEscala);
        BigDecimal montoImpuesto = new BigDecimal("16150.000000");
        when(matematicaFinancieraFalso.calcularMontoImpuesto(valorSinImpuestoConEscala, impuesto))
                .thenReturn(montoImpuesto);
        BigDecimal valorVentaPuro = new BigDecimal("101150.000000");
        BigDecimal valorVentaConEscala = new BigDecimal("101150.000000");
        when(matematicaFinancieraFalso.aplicarEscala(valorVentaPuro)).thenReturn(valorVentaConEscala);
        // ACT
        BigDecimal resultado = estrategia.calcularValorVenta(producto, contexto);
        // ASSERT
        assertEquals(valorVentaConEscala, resultado);
        verify(matematicaFinancieraFalso).calcularMontoDescuento(precioBase, descuento);
        verify(matematicaFinancieraFalso).aplicarEscala(valorSinImpuestoPuro);
        verify(matematicaFinancieraFalso).calcularMontoImpuesto(valorSinImpuestoConEscala, impuesto);
        verify(matematicaFinancieraFalso).aplicarEscala(valorVentaPuro);
        verifyNoMoreInteractions(matematicaFinancieraFalso);
    }

}//===================================================================================================================//

