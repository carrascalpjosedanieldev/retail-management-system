package RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoPerecederoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOImpuesto;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOPoliticaVencimiento;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstrategiaEnsambladoDTOPerecederoTest {

    @Mock
    private CalculadoraPrecios calculadoraPreciosFalso;

    @Mock
    private EnsambladorDTOImpuesto ensambladorDTOImpuestoFalso;

    @Mock
    private EnsambladorDTODescuento ensambladorDTODescuentoFalso;

    @Mock
    private EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimientoFalso;

    @InjectMocks
    private EstrategiaEnsambladoDTOPerecedero estrategiaEnsambladoDTOPerecedero;

    //TESTS

    @Test
    void deberiaEnsamblarDatosTotalesProductoCorrectamente(){
        //ARRANGE
        Impuesto impuesto = Impuesto.reconstruirDesdeBD(
                1, "IVA", new BigDecimal("19"), true
        );
        Descuento descuento = Descuento.reconstruirDesdeBD(
                1, "Sin Descuento", BigDecimal.ZERO, true
        );
        PoliticaVencimiento politica = PoliticaVencimiento.reconstruirDesdeBD(
                1, "Lácteos", 5, new BigDecimal("30"), true
        );
        LocalDate fechaVencimiento = LocalDate.now().plusDays(10);
        ProductoPerecedero perecedero = ProductoPerecedero.reconstruirDesdeBD(
                "PER-001", "Leche Entera", new BigDecimal("2500"), new BigDecimal("15"),
                40, impuesto, descuento, true, fechaVencimiento, politica
        );
        LocalDate fechaActual = LocalDate.now();
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(fechaActual);
        ImpuestoDTO dtoImpuesto =
                new ImpuestoDTO(1, "IVA", new BigDecimal("19"), true);
        DescuentoDTO dtoDescuento =
                new DescuentoDTO(1, "Sin Descuento", BigDecimal.ZERO, true);
        PoliticaVencimientoDTO dtoPolitica = new PoliticaVencimientoDTO(
                1, "Lácteos", 5, new BigDecimal("30"), true
        );
        BigDecimal valorVentaEsperado = new BigDecimal("3000");
        when(ensambladorDTOImpuestoFalso.ensamblarDatosImpuesto(impuesto)).thenReturn(dtoImpuesto);
        when(ensambladorDTODescuentoFalso.ensamblarDatosDescuento(descuento)).thenReturn(dtoDescuento);
        when(ensambladorDTOPoliticaVencimientoFalso.ensamblarDatosPoliticaVencimiento(politica))
                .thenReturn(dtoPolitica);
        when(calculadoraPreciosFalso.calcularValorVenta(perecedero, contexto))
                .thenReturn(valorVentaEsperado);
        //ACT
        DatosTotalesProductoPerecederoDTO resultado =
                estrategiaEnsambladoDTOPerecedero.ensamblarDatosTotalesProducto(perecedero, contexto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals("PER-001", resultado.codigo());
        assertEquals("Leche Entera", resultado.nombre());
        assertEquals(new BigDecimal("2500.000000"), resultado.valorCompra());
        assertEquals(new BigDecimal("15.000000"), resultado.porcentajeGanancia());
        assertEquals(valorVentaEsperado, resultado.valorVentaFinal());
        assertEquals(40, resultado.stock());
        assertEquals(dtoImpuesto, resultado.datosImpuesto());
        assertEquals(dtoDescuento, resultado.datosDescuento());
        assertEquals(fechaVencimiento, resultado.fechaVencimiento());
        assertEquals(dtoPolitica, resultado.datosPoliticaVencimiento());
        assertFalse(resultado.estaVencido());
        assertTrue(resultado.activo());
        verify(ensambladorDTOImpuestoFalso).ensamblarDatosImpuesto(impuesto);
        verify(ensambladorDTODescuentoFalso).ensamblarDatosDescuento(descuento);
        verify(ensambladorDTOPoliticaVencimientoFalso).ensamblarDatosPoliticaVencimiento(politica);
        verify(calculadoraPreciosFalso).calcularValorVenta(perecedero, contexto);
    }

    @Test
    void deberiaLanzarExcepcionSiElContextoNoTieneFechaAlEnsamblarDatosTotalesProducto(){
        //ARRANGE
        ProductoPerecedero perecederoMock = mock(ProductoPerecedero.class);
        ContextoEvaluacion contextoSinFechaMock = mock(ContextoEvaluacion.class);
        when(contextoSinFechaMock.getFechaEvaluacion()).thenReturn(Optional.empty());
        //ACT AND ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> estrategiaEnsambladoDTOPerecedero.ensamblarDatosTotalesProducto(
                        perecederoMock, contextoSinFechaMock
                )
        );
        assertEquals("Se Requiere una Fecha para Calcular el Valor del Producto", exception.getMessage());
        verify(contextoSinFechaMock).getFechaEvaluacion();
        verifyNoInteractions(
                calculadoraPreciosFalso, ensambladorDTODescuentoFalso, ensambladorDTOImpuestoFalso,
                ensambladorDTOPoliticaVencimientoFalso
        );
    }

    @Test
    void deberiaEnsamblarDetalleProductosCorrectamente(){
        //ARRANGE
        Impuesto impuesto = Impuesto.reconstruirDesdeBD(
                1, "IVA", new BigDecimal("19"), true
        );
        Descuento descuento = Descuento.reconstruirDesdeBD(
                1, "Sin Descuento", BigDecimal.ZERO, true
        );
        PoliticaVencimiento politica = PoliticaVencimiento.reconstruirDesdeBD(
                1, "Lácteos", 5, new BigDecimal("30"), true
        );
        LocalDate fechaVencimiento = LocalDate.now().plusDays(10);
        ProductoPerecedero perecedero1 = ProductoPerecedero.reconstruirDesdeBD(
                "PER-001", "Leche", new BigDecimal("2500"), new BigDecimal("15"),
                40, impuesto, descuento, true, fechaVencimiento, politica
        );
        ProductoPerecedero perecedero2 = ProductoPerecedero.reconstruirDesdeBD(
                "PER-002", "Yogurt", new BigDecimal("1500"), new BigDecimal("20"),
                20, impuesto, descuento, true, fechaVencimiento, politica
        );
        List<Producto> listaProductos = List.of(perecedero1, perecedero2);
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(LocalDate.now());
        ImpuestoDTO dtoImpuesto =
                new ImpuestoDTO(1, "IVA", new BigDecimal("19"), true);
        DescuentoDTO dtoDescuento =
                new DescuentoDTO(1, "Sin Descuento", BigDecimal.ZERO, true);
        PoliticaVencimientoDTO dtoPolitica =
                new PoliticaVencimientoDTO(1, "Lácteos", 5, new BigDecimal("30"), true);
        when(ensambladorDTOImpuestoFalso.ensamblarDatosImpuesto(impuesto)).thenReturn(dtoImpuesto);
        when(ensambladorDTODescuentoFalso.ensamblarDatosDescuento(descuento)).thenReturn(dtoDescuento);
        when(ensambladorDTOPoliticaVencimientoFalso.ensamblarDatosPoliticaVencimiento(politica))
                .thenReturn(dtoPolitica);
        when(calculadoraPreciosFalso.calcularValorVenta(perecedero1, contexto))
                .thenReturn(new BigDecimal("3000"));
        when(calculadoraPreciosFalso.calcularValorVenta(perecedero2, contexto))
                .thenReturn(new BigDecimal("1800"));
        //ACT
        List<DatosTotalesProductoPerecederoDTO> resultado =
                estrategiaEnsambladoDTOPerecedero.ensamblarDetalleProductos(listaProductos, contexto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("PER-001", resultado.get(0).codigo());
        assertEquals(new BigDecimal("3000"), resultado.get(0).valorVentaFinal());
        assertEquals("PER-002", resultado.get(1).codigo());
        assertEquals(new BigDecimal("1800"), resultado.get(1).valorVentaFinal());
        verify(ensambladorDTOImpuestoFalso, times(2)).ensamblarDatosImpuesto(impuesto);
        verify(ensambladorDTODescuentoFalso, times(2)).ensamblarDatosDescuento(descuento);
        verify(ensambladorDTOPoliticaVencimientoFalso, times(2))
                .ensamblarDatosPoliticaVencimiento(politica);
        verify(calculadoraPreciosFalso).calcularValorVenta(perecedero1, contexto);
        verify(calculadoraPreciosFalso).calcularValorVenta(perecedero2, contexto);
    }

}//===================================================================================================================//

