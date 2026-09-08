package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.ProductoVencidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ProductoPerecederoTest extends ProductoBaseTest<ProductoPerecedero>{

    private static final String NOMBRE_POR_DEFECTO = "Perecedero";
    private static final BigDecimal VALOR_COMPRA_POR_DEFECTO = new BigDecimal("5000");
    private static final BigDecimal PORCENTAJE_GANANCIA_POR_DEFECTO = new BigDecimal("100");
    private static final int STOCK_POR_DEFECTO = 35;
    private static final LocalDate FECHA_VENCIMIENTO_POR_DEFECTO = LocalDate.of(2028,11,19);

    @Mock
    protected PoliticaVencimiento politicaVActiva;

    @Override
    protected TipoProducto getTipoProducto() {
        return TipoProducto.PERECEDERO;
    }

    @Override
    protected ProductoPerecedero crearNuevoProducto() {
        Mockito.lenient().when(politicaVActiva.isActiva()).thenReturn(true);
        return ProductoPerecedero.crearNuevo(
                NOMBRE_POR_DEFECTO,
                VALOR_COMPRA_POR_DEFECTO,
                PORCENTAJE_GANANCIA_POR_DEFECTO,
                STOCK_POR_DEFECTO,
                impuestoActivo,
                descuentoActivo,
                FECHA_VENCIMIENTO_POR_DEFECTO,
                politicaVActiva
        );
    }

    @Test
    void deberiaLanzarExcepcionSiLaFechaDeVencimientoEsNulaAlCrearNuevo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> ProductoPerecedero.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        VALOR_COMPRA_POR_DEFECTO,
                        PORCENTAJE_GANANCIA_POR_DEFECTO,
                        STOCK_POR_DEFECTO,
                        impuestoActivo,
                        descuentoActivo,
                        null,
                        politicaVActiva
                )
        );
        assertEquals("La Fecha de Vencimiento del Producto es Obligatoria", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaPoliticaDeVencimientoEsNulaAlCrearNuevo(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> ProductoPerecedero.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        VALOR_COMPRA_POR_DEFECTO,
                        PORCENTAJE_GANANCIA_POR_DEFECTO,
                        STOCK_POR_DEFECTO,
                        impuestoActivo,
                        descuentoActivo,
                        FECHA_VENCIMIENTO_POR_DEFECTO,
                        null
                )
        );
        assertEquals("La Política de Vencimiento del Producto es Obligatoria", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiLaPoliticaDeVencimientoEstaInactivaAlCrearNuevo(){
        //ARRANGE
        PoliticaVencimiento politicaVInactiva = Mockito.mock(PoliticaVencimiento.class);
        Mockito.when(politicaVInactiva.isActiva()).thenReturn(false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> ProductoPerecedero.crearNuevo(
                        NOMBRE_POR_DEFECTO,
                        VALOR_COMPRA_POR_DEFECTO,
                        PORCENTAJE_GANANCIA_POR_DEFECTO,
                        STOCK_POR_DEFECTO,
                        impuestoActivo,
                        descuentoActivo,
                        FECHA_VENCIMIENTO_POR_DEFECTO,
                        politicaVInactiva
                )
        );
        assertEquals("La Política de Vencimiento que quieres colocar NO esta Activa", exception.getMessage());
    }

    @Test
    void deberiaCambiarPoliticaDeVencimientoCorrectamente(){
        //ARRANGE
        PoliticaVencimiento politicaVValida = Mockito.mock(PoliticaVencimiento.class);
        Mockito.when(politicaVValida.isActiva()).thenReturn(true);
        //ACT
        productoFixture.cambiarPoliticaVencimiento(politicaVValida);
        //ASSERT
        assertEquals(politicaVValida, productoFixture.getPoliticaVencimiento());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarPoliticaDeVencimientoEsNula(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.cambiarPoliticaVencimiento(null)
        );
        assertEquals("La Política de Vencimiento del Producto es Obligatoria", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarPoliticaDeVencimientoEstaInactiva(){
        //ARRANGE
        PoliticaVencimiento politicaVInvalida = Mockito.mock(PoliticaVencimiento.class);
        Mockito.when(politicaVInvalida.isActiva()).thenReturn(false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoFixture.cambiarPoliticaVencimiento(politicaVInvalida)
        );
        assertEquals("La Política de Vencimiento que quieres colocar NO esta Activa", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "2026-11-20, 2026-11-21, false",
            "2026-11-20, 2026-11-20, false",
            "2026-11-20, 2026-11-19, true",
            "2026-11-20, 2026-11-10, true"
    })
    void deberiaVerificarSiEstaVencidoCorrectamente(
            LocalDate fechaReferencia, LocalDate fechaVencimiento, boolean esperado
    ) {
        //ARRANGE
        ProductoPerecedero perecedero = ProductoPerecedero.crearNuevo(
                NOMBRE_POR_DEFECTO,
                VALOR_COMPRA_POR_DEFECTO,
                PORCENTAJE_GANANCIA_POR_DEFECTO,
                STOCK_POR_DEFECTO,
                impuestoActivo,
                descuentoActivo,
                fechaVencimiento,
                politicaVActiva
        );
        //ACT
        boolean resultado = perecedero.estaVencido(fechaReferencia);
        //ASSERT
        assertEquals(esperado, resultado);
    }

    @ParameterizedTest
    @CsvSource({
            "2026-11-20, 2026-11-19",
            "2026-11-20, 2026-11-10"
    })
    void deberiaLanzarExcepcionSiEstaVencidoAlValidarEstadoParaLaVenta(
            LocalDate fechaReferencia, LocalDate fechaVencimiento
    ) {
        //ARRANGE
        ProductoPerecedero perecedero = ProductoPerecedero.crearNuevo(
                NOMBRE_POR_DEFECTO,
                VALOR_COMPRA_POR_DEFECTO,
                PORCENTAJE_GANANCIA_POR_DEFECTO,
                STOCK_POR_DEFECTO,
                impuestoActivo,
                descuentoActivo,
                fechaVencimiento,
                politicaVActiva
        );
        //ACT AND ASSERT
        ProductoVencidoException exception = assertThrows(
                ProductoVencidoException.class,
                ()-> perecedero.validarEstadoParaVenta(fechaReferencia)
        );
        assertEquals("El Producto -" + perecedero.getNombre() + "- está vencido.", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "2026-11-20, 2026-11-21",
            "2026-11-20, 2026-11-20"
    })
    void noDeberiaLanzarExcepcionSiNoEstaVencidoAlValidarEstadoParaLaVenta(
            LocalDate fechaReferencia, LocalDate fechaVencimiento
    ) {
        //ARRANGE
        ProductoPerecedero perecedero = ProductoPerecedero.crearNuevo(
                NOMBRE_POR_DEFECTO,
                VALOR_COMPRA_POR_DEFECTO,
                PORCENTAJE_GANANCIA_POR_DEFECTO,
                STOCK_POR_DEFECTO,
                impuestoActivo,
                descuentoActivo,
                fechaVencimiento,
                politicaVActiva
        );
        //ACT AND ASSERT
        assertDoesNotThrow(()-> perecedero.validarEstadoParaVenta(fechaReferencia));
    }

    @Test
    void noDeberiaAplicarDescuentoDePoliticaSiEstaFueraDelUmbral() {
        // ARRANGE
        productoFixture.cambiarValorCompra(new BigDecimal("5000"));
        productoFixture.cambiarPorcentajeGanancia(new BigDecimal("100"));
        Mockito.when(descuentoActivo.getPorcentaje()).thenReturn(BigDecimal.ZERO);
        Mockito.when(politicaVActiva.getDiasUmbral()).thenReturn(10);
            // Simulamos que faltan 11 días (Fuera del umbral) calculándolo dinámicamente
        LocalDate fechaReferencia = productoFixture.getFechaVencimiento().minusDays(11);
        // ACT
        BigDecimal resultado = productoFixture.getValorFinalSinImpuesto(fechaReferencia);
        // ASSERT
        assertEquals(0, new BigDecimal("10000.000000").compareTo(resultado));
    }

    @ParameterizedTest
    @CsvSource({
          // compra,   ganancia, umbralPol, porcPolVen, diasRestantes, esperado
            "5000,     100,      5,         20,         4,             8000.000000",
            "2000,     50,       10,        50,         0,             1500.000000"
    })
    void deberiaAplicarDescuentoDePoliticaSiEstaDentroDelUmbral(
            String valorCompraSt, String porcentajeGananciaSt, int diasUmbral,
            String porcentajePoliticaSt, int diasRestantes, String resultadoEsperadoSt
    ) {
        // ARRANGE
        productoFixture.cambiarValorCompra(new BigDecimal(valorCompraSt));
        productoFixture.cambiarPorcentajeGanancia(new BigDecimal(porcentajeGananciaSt));
        Mockito.when(descuentoActivo.getPorcentaje()).thenReturn(BigDecimal.ZERO);
        Mockito.when(politicaVActiva.getDiasUmbral()).thenReturn(diasUmbral);
        Mockito.when(politicaVActiva.getPorcentajeDescuento()).thenReturn(new BigDecimal(porcentajePoliticaSt));
            //Calculamos dinámicamente la fecha de referencia restando diasRestantes
        LocalDate fechaReferencia = productoFixture.getFechaVencimiento().minusDays(diasRestantes);
        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
        // ACT
        BigDecimal resultado = productoFixture.getValorFinalSinImpuesto(fechaReferencia);
        // ASSERT
        assertEquals(0, resultadoEsperado.compareTo(resultado));
    }

    @Override
    @ParameterizedTest
    @CsvSource({
          // valorCompra,   ganancia, descuento, esperado,      fechaReferencia
            "85000,         30,       10,        99450.000000,   2026-12-31",
            "125000,        20,       0,         150000.000000,  2026-10-15",
            "67500,         25,       5,         80156.250000,   2026-11-20"
    })
    void deberiaCalcularElValorFinalSinImpuestoCorrectamente(
            String valorCompraSt, String porcentajeGananciaSt, String descuentoSt, String resultadoEsperadoSt,
            LocalDate fechaReferencia
    ) {
        // ARRANGE
        BigDecimal valorCompra = new BigDecimal(valorCompraSt);
        BigDecimal porcentajeGanancia = new BigDecimal(porcentajeGananciaSt);
        BigDecimal descuento = new BigDecimal(descuentoSt);
        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
        productoFixture.cambiarValorCompra(valorCompra);
        productoFixture.cambiarPorcentajeGanancia(porcentajeGanancia);
        Mockito.when(descuentoActivo.getPorcentaje()).thenReturn(descuento);
        // ACT
        BigDecimal resultado = productoFixture.getValorFinalSinImpuesto(fechaReferencia);
        // ASSERT
        assertEquals(0, resultadoEsperado.compareTo(resultado),
                "El cálculo base falló al anular la política de vencimiento");
    }

}//===================================================================================================================//

