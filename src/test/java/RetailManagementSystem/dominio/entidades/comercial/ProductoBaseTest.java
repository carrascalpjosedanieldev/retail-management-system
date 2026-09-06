package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.StockInsuficienteException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public abstract class ProductoBaseTest<T extends Producto> {

    protected abstract T crearNuevoProducto();

    protected T productoFixture;

    @Mock
    protected Impuesto impuestoActivo;

    @Mock
    protected Descuento descuentoActivo;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(impuestoActivo.isActivo()).thenReturn(true);
        Mockito.lenient().when(descuentoActivo.isActivo()).thenReturn(true);
        productoFixture = crearNuevoProducto();
    }

    //TESTS CREAR NUEVO

    @ParameterizedTest
    @CsvSource( value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElNombreEsInvalidoAlCrearNuevo(String nombreInvalido){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->{
                    new Producto(
                            nombreInvalido,
                            BigDecimal.ONE,
                            BigDecimal.ONE,
                            1,
                            impuestoActivo,
                            descuentoActivo
                    ){};
                }
        );
        assertEquals("Nombre del Producto Invalido", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElValorCompraEsInvalidoAlCrearNuevo(String valorCompraSt){
        //ARRANGE
        BigDecimal valorCompra = valorCompraSt != null ? new BigDecimal(valorCompraSt) : null;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->{
                    new Producto(
                            "Producto",
                            valorCompra,
                            BigDecimal.ONE,
                            1,
                            impuestoActivo,
                            descuentoActivo
                    ){};
                }
        );
        assertEquals("Valor de Compra del Producto Invalido", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0", "101"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElPorcentajeGananciaEsInvalidoAlCrearNuevo(String porcentajeSt){
        //ARRANGE
        BigDecimal porcentajeGanancia = porcentajeSt != null ? new BigDecimal(porcentajeSt) : null;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->{
                    new Producto(
                            "Producto",
                            BigDecimal.ONE,
                            porcentajeGanancia,
                            1,
                            impuestoActivo,
                            descuentoActivo
                    ){};
                }
        );
        assertEquals("Porcentaje de Ganancia del Producto Invalido", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElStockEsInvalidoAlCrearNuevo(Integer stock){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->{
                    new Producto(
                            "Producto",
                            BigDecimal.ONE,
                            BigDecimal.ONE,
                            stock,
                            impuestoActivo,
                            descuentoActivo
                    ){};
                }
        );
        assertEquals("Stock del Producto Invalido", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElImpuestoEsNuloAlCrearNuevo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->{
                    new Producto(
                            "Producto",
                            BigDecimal.ONE,
                            BigDecimal.ONE,
                            1,
                            null,
                            descuentoActivo
                    ){};
                }
        );
        assertEquals("El Producto Debe Tener Impuesto Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElDescuentoEsNuloAlCrearNuevo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->{
                    new Producto(
                            "Producto",
                            BigDecimal.ONE,
                            BigDecimal.ONE,
                            1,
                            impuestoActivo,
                            null
                    ){};
                }
        );
        assertEquals("El Producto Debe Tener Descuento Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElImpuestoEstaInactivoAlCrearNuevo(){
        //ARRANGE
        Mockito.when(impuestoActivo.isActivo()).thenReturn(false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->{
                    new Producto(
                            "Producto",
                            BigDecimal.ONE,
                            BigDecimal.ONE,
                            1,
                            impuestoActivo,
                            descuentoActivo
                    ){};
                }
        );
        assertEquals("El Impuesto que le quieres poner al Producto esta Inactivo", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElDescuentoEstaInactivoAlCrearNuevo(){
        //ARRANGE
        Mockito.when(descuentoActivo.isActivo()).thenReturn(false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->{
                    new Producto(
                            "Producto",
                            BigDecimal.ONE,
                            BigDecimal.ONE,
                            1,
                            impuestoActivo,
                            descuentoActivo
                    ){};
                }
        );
        assertEquals("El Descuento que le quieres poner al Producto esta Inactivo", exception.getMessage());
    }

    //TEST MÉTODOS MODIFICAR

    @ParameterizedTest
    @CsvSource({"   Modificado   ", "Modificado"})
    void deberiaCambiarNombreCorrectamente(String nombreNuevo){
        //ACT
        productoFixture.cambiarNombreProducto(nombreNuevo);
        //ASSERT
        assertEquals("Modificado", productoFixture.getNombre());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarNombreEsNuloOVacio(String nombreInvalido){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.cambiarNombreProducto(nombreInvalido)
        );
        assertEquals("Nombre del Producto Invalido", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"1", "100", "60"})
    void deberiaCambiarPorcentajeGananciaCorrectamente(String porcentajeNuevoSt){
        //ARRANGE
        BigDecimal porcentajeNuevo = new BigDecimal(porcentajeNuevoSt);
        //ACT
        productoFixture.cambiarPorcentajeGanancia(porcentajeNuevo);
        //ASSERT
        assertEquals(0, productoFixture.getPorcentajeGanancia().compareTo(porcentajeNuevo));
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0", "101"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarPorcentajeGananciaEsInvalido(String porcentajeSt){
        //ARRANGE
        BigDecimal porcentajeInvalido = porcentajeSt != null ? new BigDecimal(porcentajeSt) : null;
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.cambiarPorcentajeGanancia(porcentajeInvalido)
        );
        assertEquals("Porcentaje de Ganancia del Producto Invalido", exception.getMessage());
    }

    @Test
    void deberiaCambiarElValorCompraCorrectamente(){
        //ARRANGE
        BigDecimal valorNuevo = new BigDecimal("65000");
        //ACT
        productoFixture.cambiarValorCompra(valorNuevo);
        //ASSERT
        assertEquals(0, productoFixture.getValorCompra().compareTo(valorNuevo));
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarElValorCompraEsInvalido(String valorSt){
        //ARRANGE
        BigDecimal valorInvalido = valorSt != null ? new BigDecimal(valorSt) : null;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.cambiarValorCompra(valorInvalido)
        );
        assertEquals("Valor de Compra del Producto Invalido", exception.getMessage());
    }

    @Test
    void deberiaAumentarStockCorrectamente(){
        //ARRANGE
        int stockInicial = productoFixture.getStock();
        //ACT
        productoFixture.aumentarStock(10);
        //ASSERT
        assertEquals(stockInicial + 10, productoFixture.getStock());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlAumentarStockLaCantidadEsInvalida(Integer cantidadInvalida){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.aumentarStock(cantidadInvalida)
        );
        assertEquals("Cantidad de Producto a Reponer Invalida", exception.getMessage());
    }

    @Test
    void deberiaReducirStockCorrectamente(){
        //ARRANGE
        int cantidadAnadida = 50;
        int cantidadAReducir = 20;
        productoFixture.aumentarStock(cantidadAnadida);
        int stockPrevioAOperacion = productoFixture.getStock();
        //ACT
        productoFixture.reducirStock(cantidadAReducir);
        //ASSERT
        assertEquals(stockPrevioAOperacion - cantidadAReducir, productoFixture.getStock());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlReducirStockLaCantidadEsInvalida(Integer cantidadInvalida){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.reducirStock(cantidadInvalida)
        );
        assertEquals("Cantidad de Producto a Retirar Invalida", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaCantidadARetirarEsMayorALaExistente(){
        //ARRANGE
        int cantidadInvalida = productoFixture.getStock() + 1;
        //ACT AND ASSERT
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                ()-> productoFixture.reducirStock(cantidadInvalida)
        );
        assertEquals("La Cantidad de Producto a Reducir es Mayor a la Cantidad Existente", exception.getMessage());
    }

    @Test
    void deberiaCambiarImpuestoCorrectamente(){
        //ARRANGE
        Impuesto impuestoActivoNuevo = Mockito.mock(Impuesto.class);
        Mockito.when(impuestoActivoNuevo.isActivo()).thenReturn(true);
        //ACT
        productoFixture.cambiarImpuesto(impuestoActivoNuevo);
        //ASSERT
        assertEquals(impuestoActivoNuevo, productoFixture.getImpuesto());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarImpuestoEsNulo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.cambiarImpuesto(null)
        );
        assertEquals("El Producto Debe Tener Impuesto Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarImpuestoEstaInactivo(){
        //ARRANGE
        Impuesto impuestoInactivo = Mockito.mock(Impuesto.class);
        Mockito.when(impuestoInactivo.isActivo()).thenReturn(false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.cambiarImpuesto(impuestoInactivo)
        );
        assertEquals("El Impuesto que le quieres poner al Producto esta Inactivo", exception.getMessage());
    }

    @Test
    void deberiaCambiarDescuentoCorrectamente(){
        //ARRANGE
        Descuento descuentoActivoNuevo = Mockito.mock(Descuento.class);
        Mockito.when(descuentoActivoNuevo.isActivo()).thenReturn(true);
        //ACT
        productoFixture.cambiarDescuento(descuentoActivoNuevo);
        //ASSERT
        assertEquals(descuentoActivoNuevo, productoFixture.getDescuento());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarDescuentoEsNulo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.cambiarDescuento(null)
        );
        assertEquals("El Producto Debe Tener Descuento Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarDescuentoEstaInactivo(){
        //ARRANGE
        Descuento descuentoInactivo = Mockito.mock(Descuento.class);
        Mockito.when(descuentoInactivo.isActivo()).thenReturn(false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.cambiarDescuento(descuentoInactivo)
        );
        assertEquals("El Descuento que le quieres poner al Producto esta Inactivo", exception.getMessage());
    }

    @Test
    void deberiaCambiarEstadoCorrectamente(){
        //ARRANGE
        boolean estadoAnterior = productoFixture.isActivo();
        //ACT
        productoFixture.cambiarEstado();
        //ASSERT
        assertNotEquals(estadoAnterior, productoFixture.isActivo());
    }

    //TEST CÁLCULOS

    @ParameterizedTest
    @CsvSource({
            "10.123456, 14.50, 11.591357",
            "33333, 33.33, 44442.888900",
            "10.123456, 15.50, 11.692592",
            "10.000050, 15.00, 11.500058"
    })
    void deberiaCalcularElPrecioBaseCorrectamente(
            String valorCompraSt, String porcentajeGananciaSt, String precioBaseEsperadoSt
    ) {
        //ARRANGE
        BigDecimal valorCompra = new BigDecimal(valorCompraSt);
        BigDecimal porcentajeGanancia = new BigDecimal(porcentajeGananciaSt);
        BigDecimal precioBaseEsperado = new BigDecimal(precioBaseEsperadoSt);
        productoFixture.cambiarValorCompra(valorCompra);
        productoFixture.cambiarPorcentajeGanancia(porcentajeGanancia);
        //ACT
        BigDecimal precioBase = productoFixture.getPrecioBase();
        //ASSERT
        assertEquals(precioBaseEsperado, precioBase);
    }

    @ParameterizedTest
    @CsvSource({
            "11.592562, 36.21, 4.197667",
            "16000, 50, 8000.000000",
            "122345, 20, 24469.000000",
            "32500, 0, 0.000000"
    })
    void deberiaCalcularElDescuentoCorrectamente(
            String precioBaseSt, String porcentajeSt, String resultadoEsperadoSt
    ) {
        //ARRANGE
        BigDecimal porcentaje = new BigDecimal(porcentajeSt);
        Mockito.when(descuentoActivo.getPorcentaje()).thenReturn(porcentaje);
        BigDecimal precioBase = new BigDecimal(precioBaseSt);
        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
        //ACT
        BigDecimal resultado = productoFixture.calcularDescuento(precioBase);
        //ASSERT
        assertEquals(resultadoEsperado, resultado);
    }

    @ParameterizedTest
    @CsvSource({
            "45325.50, 19, 8611.845000",
            "122345, 8, 9787.600000",
            "32000, 0, 0.000000"
    })
    void deberiaCalcularElImpuestoCorrectamente(
            String precioBaseSt, String porcentajeSt, String resultadoEsperadoSt
    ) {
        //ARRANGE
        BigDecimal porcentaje = new BigDecimal(porcentajeSt);
        Mockito.when(impuestoActivo.getPorcentaje()).thenReturn(porcentaje);
        BigDecimal precioBase = new BigDecimal(precioBaseSt);
        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
        //ACT
        BigDecimal resultado = productoFixture.calcularImpuesto(precioBase);
        //ASSERT
        assertEquals(resultadoEsperado, resultado);
    }

    @ParameterizedTest
    @CsvSource({
            // valorCompra, ganancia, descuento, esperado,      fecha
            "85000,         30,       10,        99450.000000,   2026-12-31",
            "125000,        20,       0,         150000.000000,  2026-10-15",
            "67500,         25,       5,         80156.250000,   2026-11-20"
    })
    void deberiaCalcularElValorFinalSinImpuestoCorrectamente(
            String valorCompraSt, String porcentajeGananciaSt, String descuentoSt, String resultadoEsperadoSt,
            LocalDate fecha
    ) {
        //ARRANGE
        BigDecimal valorCompra = new BigDecimal(valorCompraSt);
        BigDecimal porcentajeGanancia = new BigDecimal(porcentajeGananciaSt);
        BigDecimal descuento = new BigDecimal(descuentoSt);
        productoFixture.cambiarValorCompra(valorCompra);
        productoFixture.cambiarPorcentajeGanancia(porcentajeGanancia);
        Mockito.when(descuentoActivo.getPorcentaje()).thenReturn(descuento);
        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
        //ACT
        BigDecimal resultado = productoFixture.getValorFinalSinImpuesto(fecha);
        //ASSERT
        assertEquals(resultadoEsperado, resultado);
    }

    @ParameterizedTest
    @CsvSource({
            // valorCompra, ganancia, descuento, impuesto, esperado,       fecha
            "85000,         30,       10,       19,       118345.500000,   2026-12-31",
            "125000,        20,       0,        19,       178500.000000,   2026-10-15",
            "67500,         25,       5,        8,        86568.750000,    2026-11-20"
    })
    void deberiaCalcularElValorVentaCorrectamente(
            String valorCompraSt, String porcentajeGananciaSt, String descuentoSt, String impuestoSt,
            String resultadoEsperadoSt, LocalDate fecha
    ) {
        //ARRANGE
        BigDecimal valorCompra = new BigDecimal(valorCompraSt);
        BigDecimal porcentajeGanancia = new BigDecimal(porcentajeGananciaSt);
        BigDecimal descuento = new BigDecimal(descuentoSt);
        BigDecimal impuesto = new BigDecimal(impuestoSt);
        productoFixture.cambiarValorCompra(valorCompra);
        productoFixture.cambiarPorcentajeGanancia(porcentajeGanancia);
        Mockito.when(descuentoActivo.getPorcentaje()).thenReturn(descuento);
        Mockito.when(impuestoActivo.getPorcentaje()).thenReturn(impuesto);
        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
        //ACT
        BigDecimal resultado = productoFixture.calcularValorVenta(fecha);
        //ASSERT
        assertEquals(resultadoEsperado, resultado);
    }

}//===================================================================================================================//

