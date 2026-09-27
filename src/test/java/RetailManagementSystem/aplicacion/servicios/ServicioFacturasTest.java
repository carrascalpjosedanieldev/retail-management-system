package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.dto.consultas.ReporteRecaudoDTO;
import RetailManagementSystem.aplicacion.dto.consultas.ResumenVentaDiaDTO;
import RetailManagementSystem.dominio.entidades.ventas.Factura;
import RetailManagementSystem.dominio.entidades.ventas.ItemVendido;
import RetailManagementSystem.dominio.enums.TipoItem;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioFacturas;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioFacturasTest {

    @Mock
    private RepositorioFacturas repositorioFacturasFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioFacturas servicioFacturas;

    @BeforeEach
    void setUp(){
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionConRetorno(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionDeLectura(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().doAnswer(invocation -> {
            OperacionTransaccional operacion = invocation.getArgument(0);
            operacion.ejecutar();
            return null;
        }).when(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    //TESTS

    @Test
    void deberiaRegistrarVentaYObtenerFacturaCorrectamente(){
        //ARRANGE
        BigDecimal precioUnitario = new BigDecimal("10000");
        int cantidad = 5;
        ItemVendido itemReal = ItemVendido.crearNuevo(
                TipoItem.PRODUCTO,
                "ItemVendido0123456789",
                "Item Vendido",
                cantidad,
                precioUnitario,
                new BigDecimal("5")
        );
        List<ItemVendido> items = List.of(itemReal);
        LocalDateTime fechaEmision = LocalDateTime.now();
        Factura facturaEsperada = Factura.reconstruirDesdeBD(
                items,
                1,
                "FAC-001",
                fechaEmision,
                itemReal.getTotalLinea(),
                itemReal.getMontoImpuesto(),
                itemReal.getSubtotalNeto()
        );
        when(repositorioFacturasFalso.insertarFactura(items)).thenReturn(facturaEsperada);
        //ACT
        Factura resultado = servicioFacturas.registrarVentaYObtenerFactura(items);
        //ASSERT
        assertEquals(facturaEsperada, resultado);
        verify(repositorioFacturasFalso).insertarFactura(items);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaLanzarExcepcionSiLaListaEsNulaAlRegistrarVentaYObtenerFactura() {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioFacturas.registrarVentaYObtenerFactura(null)
        );
        assertEquals("NO se puede Registrar una Venta Vacía.", exception.getMessage());
        verifyNoInteractions(repositorioFacturasFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiLaListaDeItemsFinalesEstaVaciaAlRegistrarVentaYObtenerFactura(){
        // ARRANGE
        List<ItemVendido> itemsVacios = List.of();
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioFacturas.registrarVentaYObtenerFactura(itemsVacios)
        );
        assertEquals("NO se puede Registrar una Venta Vacía.", exception.getMessage());
        verifyNoInteractions(gestorTransaccionalFalso, repositorioFacturasFalso);
    }

    @Test
    void deberiaObtenerReporteRecaudoCorrectamente(){
        //ARRANGE
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicial = fechaFin.minusDays(5);
        ReporteRecaudoDTO reporteEsperado = new ReporteRecaudoDTO(
                fechaInicial,
                fechaFin,
                5,
                new BigDecimal("18000"),
                new BigDecimal("2000"),
                new BigDecimal("20000")
        );
        when(repositorioFacturasFalso.obtenerReporteRecaudo(fechaInicial, fechaFin)).thenReturn(reporteEsperado);
        //ACT
        ReporteRecaudoDTO resultado = servicioFacturas.obtenerReporteRecaudo(fechaInicial, fechaFin);
        //ASSERT
        assertEquals(resultado, reporteEsperado);
        verify(repositorioFacturasFalso).obtenerReporteRecaudo(fechaInicial, fechaFin);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }

    @Test
    void deberiaLanzarExcepcionSiLasFechasSonNulasAlObtenerReporteRecaudo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioFacturas.obtenerReporteRecaudo(null, null)
        );
        assertEquals("Las Fechas para el Reporte NO pueden estar Vacías.", exception.getMessage());
        verifyNoInteractions(gestorTransaccionalFalso, repositorioFacturasFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiAlgunaFechaEsNulaAlObtenerReporteRecaudo() {
        //ARRANGE
        LocalDate fecha = LocalDate.now();
        //ACT AND ASSERT
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> servicioFacturas.obtenerReporteRecaudo(null, fecha)),
                () -> assertThrows(IllegalArgumentException.class, () -> servicioFacturas.obtenerReporteRecaudo(fecha, null))
        );
        verifyNoInteractions(gestorTransaccionalFalso, repositorioFacturasFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiLaFechaFinEstaAntesDeLaFechaInicioAlObtenerReporteRecaudo(){
        //ARRANGE
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicialInvalida = fechaFin.plusDays(5);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioFacturas.obtenerReporteRecaudo(fechaInicialInvalida, fechaFin)
        );
        assertEquals(
                "La Fecha de Inicio (" + fechaInicialInvalida + ")" +
                        " NO puede ser Posterior a la Fecha de Fin (" + fechaFin + ").",
                exception.getMessage()
        );
        verifyNoInteractions(gestorTransaccionalFalso, repositorioFacturasFalso);
    }

    @Test
    void deberiaObtenerResumenHoyCorrectamente(){
        //ARRANGE
        LocalDate fechaHoy = LocalDate.now();
        ReporteRecaudoDTO reporteEsperado = new ReporteRecaudoDTO(
                fechaHoy,
                fechaHoy,
                5,
                new BigDecimal("18000"),
                new BigDecimal("2000"),
                new BigDecimal("20000")
        );
        BigDecimal ultimaVenta = new BigDecimal("25000");
        when(repositorioFacturasFalso.obtenerReporteRecaudo(fechaHoy, fechaHoy)).thenReturn(reporteEsperado);
        when(repositorioFacturasFalso.obtenerTotalUltimaVenta(fechaHoy)).thenReturn(ultimaVenta);
        //ACT
        ResumenVentaDiaDTO resultado = servicioFacturas.obtenerResumenHoy(fechaHoy);
        //ASSERT
        assertEquals(reporteEsperado.totalRecaudo(), resultado.totalVentas());
        assertEquals(reporteEsperado.cantidadFacturasEmitidas(), resultado.cantidadFacturas());
        assertEquals(ultimaVenta, resultado.ultimaVenta());
        verify(repositorioFacturasFalso).obtenerReporteRecaudo(fechaHoy, fechaHoy);
        verify(repositorioFacturasFalso).obtenerTotalUltimaVenta(fechaHoy);
        verify(gestorTransaccionalFalso, times(2)).ejecutarEnTransaccionDeLectura(any());
    }

    @Test
    void deberiaLanzarExcepcionSiLaFechaHoyEsNulaAlObtenerResumen() {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioFacturas.obtenerResumenHoy(null)
        );
        assertEquals(
                "La Fecha NO Puede estar Vacía para Obtener el Resumen de Venta de Hoy.",
                exception.getMessage()
        );
        verifyNoInteractions(gestorTransaccionalFalso, repositorioFacturasFalso);
    }

}//===================================================================================================================//

