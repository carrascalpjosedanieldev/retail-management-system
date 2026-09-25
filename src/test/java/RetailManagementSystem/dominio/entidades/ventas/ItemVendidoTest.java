package RetailManagementSystem.dominio.entidades.ventas;

import RetailManagementSystem.dominio.enums.TipoItem;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemVendidoTest {

    private static final TipoItem TIPO_ITEM_POR_DEFECTO = TipoItem.PRODUCTO;

    private static final String CODIGO_POR_DEFECTO = "PROD-001";

    private static final String NOMBRE_POR_DEFECTO = "Camiseta de Algodón";

    private static final int CANTIDAD_POR_DEFECTO = 2;

    private static final BigDecimal PRECIO_POR_DEFECTO = new BigDecimal("50000");

    private static final BigDecimal IMPUESTO_POR_DEFECTO = new BigDecimal("19");

    //TESTS

    @ParameterizedTest
    @CsvSource({
          //precioUnitario,  cantidad, %impuesto, totalLinea, subtotalNeto, montoImpuesto
            "100,            2,        0,         200.000000,        200.000000,   0.000000",
            "119,            1,        19,        119.000000,        100.000000,   19.000000",
            "15.55,          3,        8.5,       46.650000,      42.995392,    3.654608",
            "10.50,          4,        5,         42.000000,      40.000000,    2.000000"
    })
    void deberiaCalcularTotalesYExtraerImpuestosCorrectamenteConSeisDecimales(
            String precioUnitarioSt, int cantidad, String porcentajeImpuestoSt,
            String totalLineaSt, String subtotalNetoSt, String montoImpuestoSt
    ) {
        //ARRANGE
        BigDecimal precioUnitario = new BigDecimal(precioUnitarioSt);
        BigDecimal porcentajeImpuesto = new BigDecimal(porcentajeImpuestoSt);
        BigDecimal totalLineaEsperado = new BigDecimal(totalLineaSt);
        BigDecimal subtotalNetoEsperado= new BigDecimal(subtotalNetoSt);
        BigDecimal montoImpuestoEsperado= new BigDecimal(montoImpuestoSt);
        // ACT
        ItemVendido item = ItemVendido.crearNuevo(
                TIPO_ITEM_POR_DEFECTO,
                CODIGO_POR_DEFECTO,
                NOMBRE_POR_DEFECTO,
                cantidad,
                precioUnitario,
                porcentajeImpuesto
        );
        // ASSERT
        assertEquals(totalLineaEsperado, item.getTotalLinea(), "Falla en Total Línea");
        assertEquals(
                subtotalNetoEsperado, item.getSubtotalNeto(), "Falla en Subtotal Neto (Extracción de base)"
        );
        assertEquals(
                montoImpuestoEsperado, item.getMontoImpuesto(), "Falla en Monto Impuesto (Extracción de IVA)"
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElNombreEsNuloOVacio(String nombreInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ItemVendido.crearNuevo(
                        TIPO_ITEM_POR_DEFECTO,
                        CODIGO_POR_DEFECTO,
                        nombreInvalido,
                        CANTIDAD_POR_DEFECTO,
                        PRECIO_POR_DEFECTO,
                        IMPUESTO_POR_DEFECTO
                )
        );
        assertEquals(
                "El Nombre del Item de Código -" + CODIGO_POR_DEFECTO + "- NO puede estar Vacío.",
                exception.getMessage()
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElCodigoEsNuloOVacio(String codigoInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ItemVendido.crearNuevo(
                        TIPO_ITEM_POR_DEFECTO,
                        codigoInvalido,
                        NOMBRE_POR_DEFECTO,
                        CANTIDAD_POR_DEFECTO,
                        PRECIO_POR_DEFECTO,
                        IMPUESTO_POR_DEFECTO
                )
        );
        assertEquals(
                "El Código del Item -" + NOMBRE_POR_DEFECTO + "- NO puede estar Vacío.",
                exception.getMessage()
        );
    }

    @ParameterizedTest
    @CsvSource({"0", "-1", "-10"})
    void deberiaLanzarExcepcionSiLaCantidadEsInvalida(int cantidadInvalida) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ItemVendido.crearNuevo(
                        TIPO_ITEM_POR_DEFECTO,
                        CODIGO_POR_DEFECTO,
                        NOMBRE_POR_DEFECTO,
                        cantidadInvalida,
                        PRECIO_POR_DEFECTO,
                        IMPUESTO_POR_DEFECTO
                )
        );
        assertEquals("La Cantidad del Item -" + NOMBRE_POR_DEFECTO + "- es Invalida.", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"0.0", "-1.5", "-100"})
    void deberiaLanzarExcepcionSiElPrecioUnitarioEsInvalido(String precioInvalidoSt) {
        //ARRANGE
        BigDecimal precioInvalido = new BigDecimal(precioInvalidoSt);
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ItemVendido.crearNuevo(
                        TIPO_ITEM_POR_DEFECTO,
                        CODIGO_POR_DEFECTO,
                        NOMBRE_POR_DEFECTO,
                        CANTIDAD_POR_DEFECTO,
                        precioInvalido,
                        IMPUESTO_POR_DEFECTO
                )
        );
        assertEquals(
                "El Precio Unitario del Item -" + NOMBRE_POR_DEFECTO + "- es Invalido.",
                exception.getMessage()
        );
    }

    @ParameterizedTest
    @CsvSource({"-0.1", "-1.0", "-19"})
    void deberiaLanzarExcepcionSiElPorcentajeDeImpuestoEsInvalido(String impuestoInvalidoSt) {
        //ARRANGE
        BigDecimal impuestoInvalido = new BigDecimal(impuestoInvalidoSt);
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ItemVendido.crearNuevo(
                        TIPO_ITEM_POR_DEFECTO,
                        CODIGO_POR_DEFECTO,
                        NOMBRE_POR_DEFECTO,
                        CANTIDAD_POR_DEFECTO,
                        PRECIO_POR_DEFECTO,
                        impuestoInvalido
                )
        );
        assertEquals(
                "El Porcentaje de Impuesto del Item -" + NOMBRE_POR_DEFECTO + "- es Invalido.",
                exception.getMessage()
        );
    }

}//===================================================================================================================//

