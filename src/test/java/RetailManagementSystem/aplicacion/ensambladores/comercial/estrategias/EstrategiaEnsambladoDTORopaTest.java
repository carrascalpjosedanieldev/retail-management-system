package RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoRopaDTO;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOImpuesto;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.Talla;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstrategiaEnsambladoDTORopaTest {

    @Mock
    private CalculadoraPrecios calculadoraPreciosFalso;

    @Mock
    private EnsambladorDTOImpuesto ensambladorDTOImpuestoFalso;

    @Mock
    private EnsambladorDTODescuento ensambladorDTODescuentoFalso;

    @InjectMocks
    private EstrategiaEnsambladoDTORopa estrategiaEnsambladoDTORopa;

    //TESTS

    @Test
    void deberiaEnsamblarDatosTotalesProductoCorrectamente(){
        //ARRANGE
        Impuesto impuesto = Impuesto.reconstruirDesdeBD(
                1, "IVA", new BigDecimal("19"), true
        );
        Descuento descuento = Descuento.reconstruirDesdeBD(
                1, "Black Friday", new BigDecimal("50"), true
        );
        ProductoRopa ropa = ProductoRopa.reconstruirDesdeBD(
                "ROPA-001",
                "Camisa de Lino",
                new BigDecimal("45000"),
                new BigDecimal("30"),
                25,
                impuesto,
                descuento,
                true,
                Talla.M
        );
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(LocalDate.now());
        ImpuestoDTO dtoImpuestoEsperado =
                new ImpuestoDTO(1, "IVA", new BigDecimal("19"), true);
        DescuentoDTO dtoDescuentoEsperado =
                new DescuentoDTO(1, "Black Friday", new BigDecimal("50"), true);
        BigDecimal valorVentaEsperado = new BigDecimal("58500");
        when(ensambladorDTOImpuestoFalso.ensamblarDatosImpuesto(impuesto)).thenReturn(dtoImpuestoEsperado);
        when(ensambladorDTODescuentoFalso.ensamblarDatosDescuento(descuento)).thenReturn(dtoDescuentoEsperado);
        when(calculadoraPreciosFalso.calcularValorVenta(ropa, contexto)).thenReturn(valorVentaEsperado);
        //ACT
        DatosTotalesProductoRopaDTO resultado =
                estrategiaEnsambladoDTORopa.ensamblarDatosTotalesProducto(ropa, contexto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals("ROPA-001", resultado.codigo());
        assertEquals("Camisa de Lino", resultado.nombre());
        assertEquals(new BigDecimal("45000.000000"), resultado.valorCompra());
        assertEquals(new BigDecimal("30.000000"), resultado.porcentajeGanancia());
        assertEquals(valorVentaEsperado, resultado.valorVentaFinal());
        assertEquals(25, resultado.stock());
        assertEquals(dtoImpuestoEsperado, resultado.datosImpuesto());
        assertEquals(dtoDescuentoEsperado, resultado.datosDescuento());
        assertEquals(Talla.M, resultado.talla());
        assertTrue(resultado.activo());
        verify(ensambladorDTOImpuestoFalso).ensamblarDatosImpuesto(impuesto);
        verify(ensambladorDTODescuentoFalso).ensamblarDatosDescuento(descuento);
        verify(calculadoraPreciosFalso).calcularValorVenta(ropa, contexto);
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
        ProductoRopa ropa1 = ProductoRopa.reconstruirDesdeBD(
                "ROPA-001",
                "Camisa",
                new BigDecimal("45000"),
                new BigDecimal("30"),
                25,
                impuesto,
                descuento,
                true,
                Talla.M
        );
        ProductoRopa ropa2 = ProductoRopa.reconstruirDesdeBD(
                "ROPA-002",
                "Pantalón",
                new BigDecimal("80000"),
                new BigDecimal("40"),
                10,
                impuesto,
                descuento,
                true,
                Talla.L
        );
        List<Producto> listaProductos = List.of(ropa1, ropa2);
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(LocalDate.now());
        ImpuestoDTO dtoImpuestoEsperado =
                new ImpuestoDTO(1, "IVA", new BigDecimal("19"), true);
        DescuentoDTO dtoDescuentoEsperado =
                new DescuentoDTO(1, "Sin Descuento", BigDecimal.ZERO, true);
        when(ensambladorDTOImpuestoFalso.ensamblarDatosImpuesto(impuesto)).thenReturn(dtoImpuestoEsperado);
        when(ensambladorDTODescuentoFalso.ensamblarDatosDescuento(descuento)).thenReturn(dtoDescuentoEsperado);
        when(calculadoraPreciosFalso.calcularValorVenta(ropa1, contexto)).thenReturn(new BigDecimal("58500"));
        when(calculadoraPreciosFalso.calcularValorVenta(ropa2, contexto)).thenReturn(new BigDecimal("112000"));
        //ACT
        List<DatosTotalesProductoRopaDTO> resultado =
                estrategiaEnsambladoDTORopa.ensamblarDetalleProductos(listaProductos, contexto);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("ROPA-001", resultado.getFirst().codigo());
        assertEquals(new BigDecimal("58500"), resultado.get(0).valorVentaFinal());
        assertEquals(Talla.M, resultado.get(0).talla());
        assertEquals("ROPA-002", resultado.get(1).codigo());
        assertEquals(new BigDecimal("112000"), resultado.get(1).valorVentaFinal());
        assertEquals(Talla.L, resultado.get(1).talla());
        verify(ensambladorDTOImpuestoFalso, times(2)).ensamblarDatosImpuesto(impuesto);
        verify(ensambladorDTODescuentoFalso, times(2)).ensamblarDatosDescuento(descuento);
        verify(calculadoraPreciosFalso).calcularValorVenta(ropa1, contexto);
        verify(calculadoraPreciosFalso).calcularValorVenta(ropa2, contexto);
    }

}//===================================================================================================================//

