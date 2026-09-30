package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioTest {

    private final String NOMBRE_POR_DEFECTO = "Servicio Estándar";

    private final BigDecimal PRECIO_BASE_POR_DEFECTO = new BigDecimal("5000");

    private final Impuesto impuestoActivo =
            Impuesto.crearNuevo("IVA", new BigDecimal("19"), true);

    private final Descuento descuentoActivo =
            Descuento.crearNuevo("Descuento", new BigDecimal("10"), true);

    private Servicio servicioPrueba;

    @BeforeEach
    void setUp() {
        servicioPrueba = Servicio.crearNuevo(
                NOMBRE_POR_DEFECTO,
                PRECIO_BASE_POR_DEFECTO,
                impuestoActivo,
                descuentoActivo
        );
    }

    private Impuesto crearImpuestoConPorcentaje(String porcentaje) {
        return Impuesto.crearNuevo("IMP_TEST", new BigDecimal(porcentaje), true);
    }

    private Descuento crearDescuentoConPorcentaje(String porcentaje) {
        return Descuento.crearNuevo("DESC_TEST", new BigDecimal(porcentaje), true);
    }

    //TEST'S

    @Test
    void deberiaRecuperarServicioDesdeBDCorrectamente(){
        //ARRANGE
        String codigo = UUID.randomUUID().toString();
        //ACT
        Servicio servicio = Servicio.reconstruirDesdeBD(
                codigo,
                NOMBRE_POR_DEFECTO,
                PRECIO_BASE_POR_DEFECTO,
                impuestoActivo,
                descuentoActivo,
                true
        );
        //ASSERT
        assertEquals(codigo, servicio.getCodigo());
        assertEquals(NOMBRE_POR_DEFECTO, servicio.getNombre());
        assertEquals(0, PRECIO_BASE_POR_DEFECTO.compareTo(servicio.getPrecioBase()));
        assertEquals(impuestoActivo, servicio.getImpuesto());
        assertEquals(descuentoActivo, servicio.getDescuento());
        assertTrue(servicio.isActivo());
    }

    @Test
    void deberiaLanzarExcepcionSiElCodigoEsNuloAlRecuperarDeBD(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Servicio.reconstruirDesdeBD(
                        null,
                        NOMBRE_POR_DEFECTO,
                        PRECIO_BASE_POR_DEFECTO,
                        impuestoActivo,
                        descuentoActivo,
                        true
                )
        );
        assertEquals("El Código del Servicio esta Vacío", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElCodigoSuperaLos50CaracteresAlRecuperarDeBD(){
        //ARRANGE
        String codigoInvalido = "123456789012345678901234567890123456789012345678901";
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Servicio.reconstruirDesdeBD(
                        codigoInvalido,
                        NOMBRE_POR_DEFECTO,
                        PRECIO_BASE_POR_DEFECTO,
                        impuestoActivo,
                        descuentoActivo,
                        true
                )
        );
        assertEquals("El Código del Servicio excede los Caracteres Máximos Posibles", exception.getMessage());
    }

    @Test
    void deberiaRecuperarElImpuestoInactivoDeBDYReconstruirCorrectamente(){
        //ARRANGE
        String codigo = UUID.randomUUID().toString();
        Impuesto impuestoInactivo =
                Impuesto.crearNuevo("Inactivo", new BigDecimal("15"), false);
        //ACT
        Servicio servicio = Servicio.reconstruirDesdeBD(
                codigo,
                NOMBRE_POR_DEFECTO,
                PRECIO_BASE_POR_DEFECTO,
                impuestoInactivo,
                descuentoActivo,
                true
        );
        //ASSERT
        assertEquals(impuestoInactivo, servicio.getImpuesto());
    }

    @Test
    void deberiaRecuperarElDescuentoInactivoDeBDYReconstruirCorrectamente(){
        //ARRANGE
        String codigo = UUID.randomUUID().toString();
        Descuento descuentoInactivo =
                Descuento.crearNuevo("Inactivo", new BigDecimal("15"), false);
        //ACT
        Servicio servicio = Servicio.reconstruirDesdeBD(
                codigo,
                NOMBRE_POR_DEFECTO,
                PRECIO_BASE_POR_DEFECTO,
                impuestoActivo,
                descuentoInactivo,
                true
        );
        //ASSERT
        assertEquals(descuentoInactivo, servicio.getDescuento());
    }

    @Test
    void deberiaLanzarExcepcionSiElEstadoEsNuloAlReconstruirDesdeBD(){
        //ARRANGE
        String codigo = UUID.randomUUID().toString();
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Servicio.reconstruirDesdeBD(
                        codigo,
                        NOMBRE_POR_DEFECTO,
                        PRECIO_BASE_POR_DEFECTO,
                        impuestoActivo,
                        descuentoActivo,
                        null
                )
        );
        assertEquals("El Estado del Servicio es Obligatorio", exception.getMessage());
    }

    @Test
    void deberiaCrearNuevoCorrectamente(){
        //ACT
        Servicio servicio = Servicio.crearNuevo(
                NOMBRE_POR_DEFECTO,
                PRECIO_BASE_POR_DEFECTO,
                impuestoActivo,
                descuentoActivo
        );
        //ASSERT
        assertNotNull(servicio.getCodigo());
        assertEquals(NOMBRE_POR_DEFECTO, servicio.getNombre());
        assertEquals(0, PRECIO_BASE_POR_DEFECTO.compareTo(servicio.getPrecioBase()));
        assertEquals(impuestoActivo, servicio.getImpuesto());
        assertEquals(descuentoActivo, servicio.getDescuento());
    }

    @ParameterizedTest
    @CsvSource( value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElNombreEsInvalidoAlCrearNuevo(String nombreInvalido){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Servicio.crearNuevo(
                        nombreInvalido,
                        PRECIO_BASE_POR_DEFECTO,
                        impuestoActivo,
                        descuentoActivo
                )
        );
        assertEquals("Nombre del Servicio Vacío", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElPrecioBaseEsInvalidoAlCrearNuevo(String precioBaseSt){
        //ARRANGE
        BigDecimal precioBaseInvalido = precioBaseSt != null ? new BigDecimal(precioBaseSt) : null;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Servicio.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        precioBaseInvalido,
                        impuestoActivo,
                        descuentoActivo
                )
        );
        assertEquals("Precio Base del Servicio Invalido", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElImpuestoEsNuloAlCrearNuevo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Servicio.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        PRECIO_BASE_POR_DEFECTO,
                        null,
                        descuentoActivo
                )
        );
        assertEquals("El Servicio Debe Tener Impuesto Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElDescuentoEsNuloAlCrearNuevo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Servicio.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        PRECIO_BASE_POR_DEFECTO,
                        impuestoActivo,
                        null
                )
        );
        assertEquals("El Servicio Debe Tener Descuento Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElImpuestoEstaInactivoAlCrearNuevo(){
        //ARRANGE
        Impuesto impuestoInactivo =
                Impuesto.crearNuevo("Inactivo", new BigDecimal("19"), false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Servicio.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        PRECIO_BASE_POR_DEFECTO,
                        impuestoInactivo,
                        descuentoActivo
                )
        );
        assertEquals("El Impuesto que le quieres poner al Servicio esta Inactivo", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiElDescuentoEstaInactivoAlCrearNuevo(){
        //ARRANGE
        Descuento descuentoInactivo =
                Descuento.crearNuevo("Inactivo", new BigDecimal("20"), false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Servicio.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        PRECIO_BASE_POR_DEFECTO,
                        impuestoActivo,
                        descuentoInactivo
                )
        );
        assertEquals("El Descuento que le quieres poner al Servicio esta Inactivo", exception.getMessage());
    }

    //TEST MODIFICAR

    @ParameterizedTest
    @CsvSource({"   Modificado   ", "Modificado"})
    void deberiaCambiarNombreCorrectamente(String nombreNuevo){
        //ACT
        servicioPrueba.cambiarNombreServicio(nombreNuevo);
        //ASSERT
        assertEquals("Modificado", servicioPrueba.getNombre());
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarNombreEsNuloOVacio(String nombreInvalido){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioPrueba.cambiarNombreServicio(nombreInvalido)
        );
        assertEquals("Nombre del Servicio Vacío", exception.getMessage());
    }

    @Test
    void deberiaCambiarElPrecioBaseCorrectamente(){
        //ARRANGE
        BigDecimal precioNuevo = new BigDecimal("15000");
        //ACT
        servicioPrueba.cambiarPrecioBase(precioNuevo);
        //ASSERT
        assertEquals(0, servicioPrueba.getPrecioBase().compareTo(precioNuevo));
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiAlCambiarElPrecioBaseEsInvalido(String precioSt){
        //ARRANGE
        BigDecimal precioInvalido = precioSt != null ? new BigDecimal(precioSt) : null;
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioPrueba.cambiarPrecioBase(precioInvalido)
        );
        assertEquals("Precio Base del Servicio Invalido", exception.getMessage());
    }

    @Test
    void deberiaCambiarImpuestoCorrectamente(){
        //ARRANGE
        Impuesto impuestoActivoNuevo =
                Impuesto.crearNuevo("Nuevo", new BigDecimal("5"), true);
        //ACT
        servicioPrueba.cambiarImpuesto(impuestoActivoNuevo);
        //ASSERT
        assertEquals(impuestoActivoNuevo, servicioPrueba.getImpuesto());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarImpuestoEsNulo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioPrueba.cambiarImpuesto(null)
        );
        assertEquals("El Servicio Debe Tener Impuesto Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarImpuestoEstaInactivo(){
        //ARRANGE
        Impuesto impuestoInactivo =
                Impuesto.crearNuevo("Inactivo", new BigDecimal("5"), false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioPrueba.cambiarImpuesto(impuestoInactivo)
        );
        assertEquals("El Impuesto que le quieres poner al Servicio esta Inactivo", exception.getMessage());
    }

    @Test
    void deberiaCambiarDescuentoCorrectamente(){
        //ARRANGE
        Descuento descuentoActivoNuevo =
                Descuento.crearNuevo("Nuevo", new BigDecimal("15"), true);
        //ACT
        servicioPrueba.cambiarDescuento(descuentoActivoNuevo);
        //ASSERT
        assertEquals(descuentoActivoNuevo, servicioPrueba.getDescuento());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarDescuentoEsNulo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioPrueba.cambiarDescuento(null)
        );
        assertEquals("El Servicio Debe Tener Descuento Obligatoriamente", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarDescuentoEstaInactivo(){
        //ARRANGE
        Descuento descuentoInactivo =
                Descuento.crearNuevo("Inactivo", new BigDecimal("15"), false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioPrueba.cambiarDescuento(descuentoInactivo)
        );
        assertEquals("El Descuento que le quieres poner al Servicio esta Inactivo", exception.getMessage());
    }

    @Test
    void deberiaCambiarEstadoCorrectamente(){
        //ARRANGE
        boolean estadoAnterior = servicioPrueba.isActivo();
        //ACT
        servicioPrueba.cambiarEstado();
        //ASSERT
        assertNotEquals(estadoAnterior, servicioPrueba.isActivo());
    }

    //TEST CÁLCULOS

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
//        BigDecimal precioBase = new BigDecimal(precioBaseSt);
//        Servicio servicio = Servicio.crearNuevo(
//                NOMBRE_POR_DEFECTO,
//                precioBase,
//                impuestoActivo,
//                descuento
//        );
//        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
//        //ACT
//        BigDecimal resultado = servicio.calcularDescuento(precioBase);
//        //ASSERT
//        assertEquals(resultadoEsperado, resultado);
//    }

//    @Test
//    void deberiaCalcularElDescuentoAunqueEsteInactivo(){
//        //ARRANGE
//        String codigo = UUID.randomUUID().toString();
//        Descuento descuento = Descuento.crearNuevo("Inactivo", new BigDecimal("25"), false);
//        BigDecimal precioBase = new BigDecimal("15000");
//        Servicio servicio = Servicio.reconstruirDesdeBD(
//                codigo,
//                NOMBRE_POR_DEFECTO,
//                precioBase,
//                impuestoActivo,
//                descuento,
//                true
//        );
//        BigDecimal resultadoEsperado = new BigDecimal("0.000000");
//        //ACT
//        BigDecimal resultado = servicio.calcularDescuento(precioBase);
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
//        BigDecimal precioBase = new BigDecimal(precioBaseSt);
//        Servicio servicio = Servicio.crearNuevo(
//                NOMBRE_POR_DEFECTO,
//                precioBase,
//                impuesto,
//                descuentoActivo
//        );
//        BigDecimal precioFinalSinImpuesto = new BigDecimal(precioBaseSt);
//        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
//        //ACT
//        BigDecimal resultado = servicio.calcularImpuesto(precioFinalSinImpuesto);
//        //ASSERT
//        assertEquals(resultadoEsperado, resultado);
//    }

//    @Test
//    void deberiaCalcularElImpuestoAunqueEsteInactivo(){
//        //ARRANGE
//        String codigo = UUID.randomUUID().toString();
//        Impuesto impuesto = Impuesto.crearNuevo("Inactivo", new BigDecimal("25"), false);
//        BigDecimal precioBase = new BigDecimal("15000");
//        Servicio servicio = Servicio.reconstruirDesdeBD(
//                codigo,
//                NOMBRE_POR_DEFECTO,
//                precioBase,
//                impuesto,
//                descuentoActivo,
//                true
//        );
//        BigDecimal resultadoEsperado = new BigDecimal("0.000000");
//        //ACT
//        BigDecimal resultado = servicio.calcularImpuesto(precioBase);
//        //ASSERT
//        assertEquals(resultadoEsperado, resultado);
//    }

//    @ParameterizedTest
//    @CsvSource({
//          //precioBase      descuento    esperado        fecha
//            "1500000,       25.5,        1117500.000000, 2026-12-31",
//            "99900,         33.333333,   66600.033300,   2026-12-31",
//            "84033.613445,  10,          75630.252100,   2026-12-31"
//    })
//    void deberiaCalcularElValorFinalSinImpuestoCorrectamente(
//            String precioBaseSt, String descuentoSt, String resultadoEsperadoSt, LocalDate fecha
//    ) {
//        //ARRANGE
//        Descuento descuento = crearDescuentoConPorcentaje(descuentoSt);
//        BigDecimal precioBase = new BigDecimal(precioBaseSt);
//        Servicio servicio = Servicio.crearNuevo(
//                NOMBRE_POR_DEFECTO,
//                precioBase,
//                impuestoActivo,
//                descuento
//        );
//        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
//        //ACT
//        BigDecimal resultado = servicio.getValorFinalSinImpuesto(fecha);
//        //ASSERT
//        assertEquals(resultadoEsperado, resultado);
//    }

//    @ParameterizedTest
//    @CsvSource({
//          //precioBase       descuento   impuesto   esperado         fecha
//            "3500000,        17.55,      19,        3434042.500000,  2026-12-31",
//            "75546.218487,   0,          19,        89900.000000,    2026-12-31",
//            "3333.333344,    10,         5,         3150.000011,     2026-12-31",
//            "2999900,        5,          0,         2849905.000000,  2026-12-31"
//    })
//    void deberiaCalcularElValorVentaCorrectamente(
//            String precioBaseSt, String descuentoSt, String impuestoSt, String resultadoEsperadoSt, LocalDate fecha
//    ) {
//        //ARRANGE
//        Impuesto impuesto = crearImpuestoConPorcentaje(impuestoSt);
//        Descuento descuento = crearDescuentoConPorcentaje(descuentoSt);
//        BigDecimal precioBase = new BigDecimal(precioBaseSt);
//        Servicio servicio = Servicio.crearNuevo(
//                NOMBRE_POR_DEFECTO,
//                precioBase,
//                impuesto,
//                descuento
//        );
//        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
//        //ACT
//        BigDecimal resultado = servicio.getValorVenta(fecha);
//        //ASSERT
//        assertEquals(resultadoEsperado, resultado);
//    }

}//===================================================================================================================//

