package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.StockInsuficienteException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ProductoBaseTest<T extends Producto> {

    protected abstract T crearNuevoProducto();

    protected T productoPrueba;

    protected final Impuesto impuestoActivo = Impuesto.crearNuevo("IVA", new BigDecimal("19"), true);

    protected final Descuento descuentoActivo = Descuento.crearNuevo("Descuento", new BigDecimal("15"), true);

    protected abstract TipoProducto getTipoProducto();

    @BeforeEach
    void setUp() {
        productoPrueba = crearNuevoProducto();
    }

    protected Impuesto crearImpuestoConPorcentaje(String porcentaje) {
        return Impuesto.crearNuevo("IMP_TEST", new BigDecimal(porcentaje), true);
    }

    protected Descuento crearDescuentoConPorcentaje(String porcentaje) {
        return Descuento.crearNuevo("DESC_TEST", new BigDecimal(porcentaje), true);
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
                            descuentoActivo,
                            getTipoProducto()
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
                            descuentoActivo,
                            getTipoProducto()
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
                            descuentoActivo,
                            getTipoProducto()
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
                            descuentoActivo,
                            getTipoProducto()
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
                            descuentoActivo,
                            getTipoProducto()
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
                            null,
                            getTipoProducto()
                    ){};
                }
        );
        assertEquals("El Producto Debe Tener Descuento Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElImpuestoEstaInactivoAlCrearNuevo(){
        //ARRANGE
        Impuesto impuestoInactivo = Impuesto.crearNuevo("Inactivo", new BigDecimal("20"), false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()->{
                    new Producto(
                            "Producto",
                            BigDecimal.ONE,
                            BigDecimal.ONE,
                            1,
                            impuestoInactivo,
                            descuentoActivo,
                            getTipoProducto()
                    ){};
                }
        );
        assertEquals("El Impuesto que le quieres poner al Producto esta Inactivo", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElDescuentoEstaInactivoAlCrearNuevo(){
        //ARRANGE
        Descuento descuentoInactivo = Descuento.crearNuevo("Inactivo", new BigDecimal("5"), false);
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
                            descuentoInactivo,
                            getTipoProducto()
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
        productoPrueba.cambiarNombreProducto(nombreNuevo);
        //ASSERT
        assertEquals("Modificado", productoPrueba.getNombre());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarNombreEsNuloOVacio(String nombreInvalido){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.cambiarNombreProducto(nombreInvalido)
        );
        assertEquals("Nombre del Producto Invalido", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"1", "100", "60"})
    void deberiaCambiarPorcentajeGananciaCorrectamente(String porcentajeNuevoSt){
        //ARRANGE
        BigDecimal porcentajeNuevo = new BigDecimal(porcentajeNuevoSt);
        //ACT
        productoPrueba.cambiarPorcentajeGanancia(porcentajeNuevo);
        //ASSERT
        assertEquals(0, productoPrueba.getPorcentajeGanancia().compareTo(porcentajeNuevo));
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0", "101"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarPorcentajeGananciaEsInvalido(String porcentajeSt){
        //ARRANGE
        BigDecimal porcentajeInvalido = porcentajeSt != null ? new BigDecimal(porcentajeSt) : null;
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.cambiarPorcentajeGanancia(porcentajeInvalido)
        );
        assertEquals("Porcentaje de Ganancia del Producto Invalido", exception.getMessage());
    }

    @Test
    void deberiaCambiarElValorCompraCorrectamente(){
        //ARRANGE
        BigDecimal valorNuevo = new BigDecimal("65000");
        //ACT
        productoPrueba.cambiarValorCompra(valorNuevo);
        //ASSERT
        assertEquals(0, productoPrueba.getValorCompra().compareTo(valorNuevo));
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarElValorCompraEsInvalido(String valorSt){
        //ARRANGE
        BigDecimal valorInvalido = valorSt != null ? new BigDecimal(valorSt) : null;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.cambiarValorCompra(valorInvalido)
        );
        assertEquals("Valor de Compra del Producto Invalido", exception.getMessage());
    }

    @Test
    void deberiaAumentarStockCorrectamente(){
        //ARRANGE
        int stockInicial = productoPrueba.getStock();
        //ACT
        productoPrueba.aumentarStock(10);
        //ASSERT
        assertEquals(stockInicial + 10, productoPrueba.getStock());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlAumentarStockLaCantidadEsInvalida(Integer cantidadInvalida){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.aumentarStock(cantidadInvalida)
        );
        assertEquals("Cantidad de Producto a Reponer Invalida", exception.getMessage());
    }

    @Test
    void deberiaReducirStockCorrectamente(){
        //ARRANGE
        int cantidadAnadida = 50;
        int cantidadAReducir = 20;
        productoPrueba.aumentarStock(cantidadAnadida);
        int stockPrevioAOperacion = productoPrueba.getStock();
        //ACT
        productoPrueba.reducirStock(cantidadAReducir);
        //ASSERT
        assertEquals(stockPrevioAOperacion - cantidadAReducir, productoPrueba.getStock());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlReducirStockLaCantidadEsInvalida(Integer cantidadInvalida){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.reducirStock(cantidadInvalida)
        );
        assertEquals("Cantidad de Producto a Retirar Invalida", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaCantidadARetirarEsMayorALaExistente(){
        //ARRANGE
        int cantidadInvalida = productoPrueba.getStock() + 1;
        //ACT AND ASSERT
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                ()-> productoPrueba.reducirStock(cantidadInvalida)
        );
        assertEquals("La Cantidad de Producto a Reducir es Mayor a la Cantidad Existente", exception.getMessage());
    }

    @Test
    void deberiaCambiarImpuestoCorrectamente(){
        //ARRANGE
        Impuesto impuestoActivoNuevo = Impuesto.crearNuevo("Nuevo", new BigDecimal("5"), true);
        //ACT
        productoPrueba.cambiarImpuesto(impuestoActivoNuevo);
        //ASSERT
        assertEquals(impuestoActivoNuevo, productoPrueba.getImpuesto());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarImpuestoEsNulo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.cambiarImpuesto(null)
        );
        assertEquals("El Producto Debe Tener Impuesto Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarImpuestoEstaInactivo(){
        //ARRANGE
        Impuesto impuestoInactivo = Impuesto.crearNuevo("Nuevo", new BigDecimal("5"), false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.cambiarImpuesto(impuestoInactivo)
        );
        assertEquals("El Impuesto que le quieres poner al Producto esta Inactivo", exception.getMessage());
    }

    @Test
    void deberiaCambiarDescuentoCorrectamente(){
        //ARRANGE
        Descuento descuentoActivoNuevo = Descuento.crearNuevo("Nuevo", new BigDecimal("10"), true);
        //ACT
        productoPrueba.cambiarDescuento(descuentoActivoNuevo);
        //ASSERT
        assertEquals(descuentoActivoNuevo, productoPrueba.getDescuento());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarDescuentoEsNulo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.cambiarDescuento(null)
        );
        assertEquals("El Producto Debe Tener Descuento Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarDescuentoEstaInactivo(){
        //ARRANGE
        Descuento descuentoInactivo = Descuento.crearNuevo("Inactivo", new BigDecimal("10"), false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.cambiarDescuento(descuentoInactivo)
        );
        assertEquals("El Descuento que le quieres poner al Producto esta Inactivo", exception.getMessage());
    }

    @Test
    void deberiaCambiarEstadoCorrectamente(){
        //ARRANGE
        boolean estadoAnterior = productoPrueba.isActivo();
        //ACT
        productoPrueba.cambiarEstado();
        //ASSERT
        assertNotEquals(estadoAnterior, productoPrueba.isActivo());
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
        productoPrueba.cambiarValorCompra(valorCompra);
        productoPrueba.cambiarPorcentajeGanancia(porcentajeGanancia);
        //ACT
        BigDecimal precioBase = productoPrueba.getPrecioBase();
        //ASSERT
        assertEquals(precioBaseEsperado, precioBase);
    }

//    @ParameterizedTest
//    @CsvSource({
//            "11.592562, 36.21, 4.197667",
//            "16000, 50, 8000.000000",
//            "122345, 20, 24469.000000",
//            "32500, 0, 0.000000"
//    })
//    void deberiaCalcularElDescuentoCorrectamente(
//            String precioBaseSt, String porcentajeSt, String resultadoEsperadoSt
//    ) {
//        //ARRANGE
//        Descuento descuento = crearDescuentoConPorcentaje(porcentajeSt);
//        productoPrueba.cambiarDescuento(descuento);
//        BigDecimal precioBase = new BigDecimal(precioBaseSt);
//        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
//        //ACT
//        BigDecimal resultado = productoPrueba.calcularDescuento(precioBase);
//        //ASSERT
//        assertEquals(resultadoEsperado, resultado);
//    }

//    @ParameterizedTest
//    @CsvSource({
//            "45325.50, 19, 8611.845000",
//            "122345, 8, 9787.600000",
//            "32000, 0, 0.000000"
//    })
//    void deberiaCalcularElImpuestoCorrectamente(
//            String precioBaseSt, String porcentajeSt, String resultadoEsperadoSt
//    ) {
//        //ARRANGE
//        Impuesto impuesto = crearImpuestoConPorcentaje(porcentajeSt);
//        productoPrueba.cambiarImpuesto(impuesto);
//        BigDecimal precioFinalSinImpuesto = new BigDecimal(precioBaseSt);
//        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
//        //ACT
//        BigDecimal resultado = productoPrueba.calcularImpuesto(precioFinalSinImpuesto);
//        //ASSERT
//        assertEquals(resultadoEsperado, resultado);
//    }

//    @ParameterizedTest
//    @CsvSource({
//            // valorCompra, ganancia, descuento, esperado,      fecha
//            "85000,         30,       10,        99450.000000,   2026-12-31",
//            "125000,        20,       0,         150000.000000,  2026-10-15",
//            "67500,         25,       5,         80156.250000,   2026-11-20"
//    })
//    void deberiaCalcularElValorFinalSinImpuestoCorrectamente(
//            String valorCompraSt, String porcentajeGananciaSt, String descuentoSt, String resultadoEsperadoSt,
//            LocalDate fecha
//    ) {
//        //ARRANGE
//        BigDecimal valorCompra = new BigDecimal(valorCompraSt);
//        BigDecimal porcentajeGanancia = new BigDecimal(porcentajeGananciaSt);
//        productoPrueba.cambiarValorCompra(valorCompra);
//        productoPrueba.cambiarPorcentajeGanancia(porcentajeGanancia);
//        Descuento descuento = crearDescuentoConPorcentaje(descuentoSt);
//        productoPrueba.cambiarDescuento(descuento);
//        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
//        //ACT
//        BigDecimal resultado = productoPrueba.getValorFinalSinImpuesto(fecha);
//        //ASSERT
//        assertEquals(resultadoEsperado, resultado);
//    }

//    @ParameterizedTest
//    @CsvSource({
//            // valorCompra,  ganancia,  descuento,  impuesto,  esperado,        fecha
//            "85000,          30,        10,         19,        118345.500000,   2026-12-31",
//            "125000,         20,        0,          19,        178500.000000,   2026-10-15",
//            "67500,          25,        5,          8,         86568.750000,    2026-11-20"
//    })
//    void deberiaCalcularElValorVentaCorrectamente(
//            String valorCompraSt, String porcentajeGananciaSt, String descuentoSt, String impuestoSt,
//            String resultadoEsperadoSt, LocalDate fecha
//    ) {
//        //ARRANGE
//        BigDecimal valorCompra = new BigDecimal(valorCompraSt);
//        BigDecimal porcentajeGanancia = new BigDecimal(porcentajeGananciaSt);
//        productoPrueba.cambiarValorCompra(valorCompra);
//        productoPrueba.cambiarPorcentajeGanancia(porcentajeGanancia);
//        Impuesto impuesto = crearImpuestoConPorcentaje(impuestoSt);
//        Descuento descuento = crearDescuentoConPorcentaje(descuentoSt);
//        productoPrueba.cambiarImpuesto(impuesto);
//        productoPrueba.cambiarDescuento(descuento);
//        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
//        //ACT
//        BigDecimal resultado = productoPrueba.calcularValorVenta(fecha);
//        //ASSERT
//        assertEquals(resultadoEsperado, resultado);
//    }

}//===================================================================================================================//

