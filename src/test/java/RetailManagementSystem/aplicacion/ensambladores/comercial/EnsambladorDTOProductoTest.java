package RetailManagementSystem.aplicacion.ensambladores.comercial;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoRopaDTO;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias.EstrategiaEnsambladoDTOProducto;
import RetailManagementSystem.aplicacion.ensambladores.comercial.estrategias.EstrategiaEnsambladoDTORopa;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnsambladorDTOProductoTest {
    
    @Mock
    private EstrategiaEnsambladoDTORopa estrategiaEnsambladoDTORopaFalsa;

    @Mock
    private CalculadoraPrecios calculadoraPreciosFalso;
    
    private EnsambladorDTOProducto ensambladorDTOProducto;
    
    @BeforeEach
    void setUp(){
        Map<TipoProducto, EstrategiaEnsambladoDTOProducto<?>> estrategiasEnsamblado = new HashMap<>();
        estrategiasEnsamblado.put(TipoProducto.ROPA, estrategiaEnsambladoDTORopaFalsa);
        ensambladorDTOProducto = new EnsambladorDTOProducto(estrategiasEnsamblado, calculadoraPreciosFalso);
    }

    //TESTS

    @Test
    void deberiaEnsamblarDatosTotalesProductoCorrectamente(){
        //ARRANGE
        ProductoRopa ropa = mock(ProductoRopa.class);
        when(ropa.getTipoProducto()).thenReturn(TipoProducto.ROPA);
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        DatosTotalesProductoRopaDTO dtoEsperado = mock(DatosTotalesProductoRopaDTO.class);
        when(estrategiaEnsambladoDTORopaFalsa.ensamblarDatosTotalesProducto(ropa, contextoEvaluacion))
                .thenReturn(dtoEsperado);
        //ACT
        DatosTotalesProductoRopaDTO resultado =
                ensambladorDTOProducto.ensamblarDatosTotalesProducto(ropa, contextoEvaluacion);
        //ASSERT
        assertSame(dtoEsperado, resultado);
        verify(estrategiaEnsambladoDTORopaFalsa).ensamblarDatosTotalesProducto(ropa, contextoEvaluacion);
        verifyNoMoreInteractions(estrategiaEnsambladoDTORopaFalsa);
        verifyNoInteractions(calculadoraPreciosFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoHayUnaEstrategiaRegistradaParaUnTipoDeProductoAlEnsamblarDatosTotalesProducto(){
        //ARRANGE
        ProductoPerecedero perecedero = mock(ProductoPerecedero.class);
        when(perecedero.getTipoProducto()).thenReturn(TipoProducto.PERECEDERO);
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        //ACT AND ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                ()-> ensambladorDTOProducto.ensamblarDatosTotalesProducto(perecedero, contextoEvaluacion)
        );
        assertEquals(
                "No existe una estrategia de ensamblado registrada para: " + perecedero.getTipoProducto(),
                exception.getMessage()
        );
        verifyNoInteractions(estrategiaEnsambladoDTORopaFalsa, calculadoraPreciosFalso);
    }

    @Test
    void deberiaRetornarUnaListaVaciaSiLaListaRecibidaEstaVaciaAlEnsamblarDetalleProductos(){
        //ARRANGE
        List<ProductoRopa> listaVacia = new ArrayList<>();
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        //ACT
        List<DatosTotalesProductoRopaDTO> listaRecibida =
                ensambladorDTOProducto.ensamblarDetalleProductos(listaVacia, contextoEvaluacion);
        //ASSERT
        assertEquals(List.of(), listaRecibida);
        verifyNoInteractions(estrategiaEnsambladoDTORopaFalsa, calculadoraPreciosFalso);
    }

    @Test
    void deberiaEnsamblarDetalleProductosCorrectamente(){
        //ARRANGE
        ProductoRopa ropa1 = mock(ProductoRopa.class);
        when(ropa1.getTipoProducto()).thenReturn(TipoProducto.ROPA);
        ProductoRopa ropa2 = mock(ProductoRopa.class);
        when(ropa2.getTipoProducto()).thenReturn(TipoProducto.ROPA);
        List<ProductoRopa> listaProductos = List.of(ropa1, ropa2);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(LocalDate.now());
        DatosTotalesProductoRopaDTO dtoEsperado1 = mock(DatosTotalesProductoRopaDTO.class);
        DatosTotalesProductoRopaDTO dtoEsperado2 = mock(DatosTotalesProductoRopaDTO.class);
        when(estrategiaEnsambladoDTORopaFalsa.ensamblarDatosTotalesProducto(ropa1, contextoEvaluacion))
                .thenReturn(dtoEsperado1);
        when(estrategiaEnsambladoDTORopaFalsa.ensamblarDatosTotalesProducto(ropa2, contextoEvaluacion))
                .thenReturn(dtoEsperado2);
        //ACT
        List<DatosTotalesProductoRopaDTO> resultado =
                ensambladorDTOProducto.ensamblarDetalleProductos(listaProductos, contextoEvaluacion);
        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertSame(dtoEsperado1, resultado.get(0));
        assertSame(dtoEsperado2, resultado.get(1));
        verify(estrategiaEnsambladoDTORopaFalsa).ensamblarDatosTotalesProducto(ropa1, contextoEvaluacion);
        verify(estrategiaEnsambladoDTORopaFalsa).ensamblarDatosTotalesProducto(ropa2, contextoEvaluacion);
        verifyNoMoreInteractions(estrategiaEnsambladoDTORopaFalsa);
        verifyNoInteractions(calculadoraPreciosFalso);
    }

    @Test
    void deberiaEnsamblarProductoResumenCorrectamente(){
        //ARRANGE
        ProductoRopa productoRopa = ProductoRopa.reconstruirDesdeBD(
                "productoRopa01234567890",
                "Ropa",
                new BigDecimal("25000"),
                new BigDecimal("80"),
                20,
                mock(Impuesto.class),
                mock(Descuento.class),
                true,
                Talla.M
        );
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        BigDecimal valorVenta = new BigDecimal("50000");
        when(calculadoraPreciosFalso.calcularValorVenta(productoRopa, contextoEvaluacion)).thenReturn(valorVenta);
        ProductoResumenDTO dtoEsperado = new ProductoResumenDTO(
                productoRopa.getCodigo(), productoRopa.getNombre(), valorVenta, productoRopa.getStock(),
                productoRopa.isActivo()
        );
        //ACT
        ProductoResumenDTO dto = ensambladorDTOProducto.ensamblarProductoResumen(productoRopa, contextoEvaluacion);
        //ASSERT
        assertEquals(dtoEsperado, dto);
        verify(calculadoraPreciosFalso).calcularValorVenta(productoRopa, contextoEvaluacion);
        verifyNoMoreInteractions(calculadoraPreciosFalso);
        verifyNoInteractions(estrategiaEnsambladoDTORopaFalsa);
    }

    @Test
    void deberiaEnsamblarDetalleProductosResumenCorrectamente(){
        // ARRANGE
        ProductoRopa productoRopa1 = ProductoRopa.reconstruirDesdeBD(
                "productoRopa01234567890",
                "Camisa",
                new BigDecimal("25000"),
                new BigDecimal("80"),
                20,
                mock(Impuesto.class),
                mock(Descuento.class),
                true,
                Talla.M
        );
        ProductoRopa productoRopa2 = ProductoRopa.reconstruirDesdeBD(
                "ropaProducto01234567890",
                "Pantalón",
                new BigDecimal("40000"),
                new BigDecimal("70"),
                15,
                mock(Impuesto.class),
                mock(Descuento.class),
                true,
                Talla.L
        );
        List<Producto> listaProductos = List.of(productoRopa1, productoRopa2);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(LocalDate.now());
        BigDecimal valorVenta1 = new BigDecimal("50000");
        BigDecimal valorVenta2 = new BigDecimal("80000");
        when(calculadoraPreciosFalso.calcularValorVenta(productoRopa1, contextoEvaluacion)).thenReturn(valorVenta1);
        when(calculadoraPreciosFalso.calcularValorVenta(productoRopa2, contextoEvaluacion)).thenReturn(valorVenta2);
        ProductoResumenDTO dtoEsperado1 = new ProductoResumenDTO(
                productoRopa1.getCodigo(), productoRopa1.getNombre(), valorVenta1, productoRopa1.getStock(),
                productoRopa1.isActivo()
        );
        ProductoResumenDTO dtoEsperado2 = new ProductoResumenDTO(
                productoRopa2.getCodigo(), productoRopa2.getNombre(), valorVenta2, productoRopa2.getStock(),
                productoRopa2.isActivo()
        );
        List<ProductoResumenDTO> listaEsperada = List.of(dtoEsperado1, dtoEsperado2);
        //ACT
        List<ProductoResumenDTO> resultado =
                ensambladorDTOProducto.ensamblarDetalleProductosResumen(listaProductos, contextoEvaluacion);
        //ASSERT
        assertEquals(listaEsperada, resultado);
        verify(calculadoraPreciosFalso).calcularValorVenta(productoRopa1, contextoEvaluacion);
        verify(calculadoraPreciosFalso).calcularValorVenta(productoRopa2, contextoEvaluacion);
        verifyNoMoreInteractions(calculadoraPreciosFalso);
        verifyNoInteractions(estrategiaEnsambladoDTORopaFalsa);
    }

}//===================================================================================================================//

