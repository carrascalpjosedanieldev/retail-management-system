package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.ProductoVencidoException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ProductoPerecederoTest extends ProductoBaseTest<ProductoPerecedero>{

    private static final String NOMBRE_POR_DEFECTO = "Perecedero";

    private static final BigDecimal VALOR_COMPRA_POR_DEFECTO = new BigDecimal("5000");

    private static final BigDecimal PORCENTAJE_GANANCIA_POR_DEFECTO = new BigDecimal("100");

    private static final int STOCK_POR_DEFECTO = 35;

    private static final LocalDate FECHA_VENCIMIENTO_POR_DEFECTO = LocalDate.of(2028,11,19);

    private final PoliticaVencimiento politicaVActiva = PoliticaVencimiento.reconstruirDesdeBD(
            1, "Política", 3, new BigDecimal("15"), true
    );

    @Override
    protected TipoProducto getTipoProducto() {
        return TipoProducto.PERECEDERO;
    }

    @Override
    protected ProductoPerecedero crearNuevoProducto() {
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
        PoliticaVencimiento politicaVInactiva = PoliticaVencimiento.reconstruirDesdeBD(
                1, "Inactiva", 3, new BigDecimal("15"), false
        );
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
        PoliticaVencimiento politicaVValida =
                PoliticaVencimiento.crearNuevo("Nueva", 3, new BigDecimal("15"), true);
        //ACT
        productoPrueba.cambiarPoliticaVencimiento(politicaVValida);
        //ASSERT
        assertEquals(politicaVValida, productoPrueba.getPoliticaVencimiento());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarPoliticaDeVencimientoEsNula(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.cambiarPoliticaVencimiento(null)
        );
        assertEquals("La Política de Vencimiento del Producto es Obligatoria", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarPoliticaDeVencimientoEstaInactiva(){
        //ARRANGE
        PoliticaVencimiento politicaVInvalida =
                PoliticaVencimiento.crearNuevo("Inactiva", 3, new BigDecimal("15"), false);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> productoPrueba.cambiarPoliticaVencimiento(politicaVInvalida)
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
        productoPrueba.cambiarValorCompra(new BigDecimal("5000"));
        productoPrueba.cambiarPorcentajeGanancia(new BigDecimal("100"));
        int diasUmbralPreparado = 10;
        PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(
                1, "Política", diasUmbralPreparado, BigDecimal.ZERO, true
        );
        Descuento descuento = Descuento.reconstruirDesdeBD(
                1, "Descuento", new BigDecimal("0"), true
        );
        productoPrueba.cambiarPoliticaVencimiento(politicaVencimiento);
        productoPrueba.cambiarDescuento(descuento);
            // Simulamos que faltan 11 días (Fuera del umbral) calculándolo dinámicamente
        LocalDate fechaReferencia = productoPrueba.getFechaVencimiento().minusDays(diasUmbralPreparado + 1);
        // ACT
        BigDecimal resultado =
                productoPrueba.calcularDescuentoPolitica(productoPrueba.getPrecioBase(), fechaReferencia);
        // ASSERT
        assertEquals(0, new BigDecimal("0.000000").compareTo(resultado));
    }

    @ParameterizedTest
    @CsvSource({
          // compra,   ganancia, umbralPol, porcPolVen, diasRestantes, esperado
            "5000,     100,      5,         20,         4,             2000.000000",
            "2000,     50,       10,        50,         0,             1500.000000"
    })
    void deberiaAplicarDescuentoDePoliticaSiEstaDentroDelUmbral(
            String valorCompraSt, String porcentajeGananciaSt, int diasUmbral,
            String porcentajePoliticaSt, int diasRestantes, String resultadoEsperadoSt
    ) {
        // ARRANGE
        productoPrueba.cambiarValorCompra(new BigDecimal(valorCompraSt));
        productoPrueba.cambiarPorcentajeGanancia(new BigDecimal(porcentajeGananciaSt));
        PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(
                1, "Política", diasUmbral, new BigDecimal(porcentajePoliticaSt), true
        );
        productoPrueba.cambiarPoliticaVencimiento(politicaVencimiento);
            //Calculamos dinámicamente la fecha de referencia restando diasRestantes
        LocalDate fechaReferencia = productoPrueba.getFechaVencimiento().minusDays(diasRestantes);
        BigDecimal resultadoEsperado = new BigDecimal(resultadoEsperadoSt);
        // ACT
        BigDecimal resultado =
                productoPrueba.calcularDescuentoPolitica(productoPrueba.getPrecioBase(), fechaReferencia);
        // ASSERT
        assertEquals(0, resultadoEsperado.compareTo(resultado));
    }

}//===================================================================================================================//

