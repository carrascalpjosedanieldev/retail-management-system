package RetailManagementSystem.dominio.financiero.calculos;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class MatematicaFinancieraTest {

    private final MatematicaFinanciera matematicaFinanciera = new MatematicaFinanciera();

    @ParameterizedTest
    @CsvSource({
            "15000,         15000.000000",
            "15000.1234564, 15000.123456",
            "15000.1234565, 15000.123457",
            "15000.1234566, 15000.123457"
    })
    void deberiaAplicarEscalaDeSeisDecimalesYRedondeoHalfUp(String entrada, String esperado) {
        // ACT
        BigDecimal resultado = matematicaFinanciera.aplicarEscala(new BigDecimal(entrada));
        // ASSERT
        assertEquals(new BigDecimal(esperado), resultado);
    }

    @ParameterizedTest
    @CsvSource({
            "19,        0.190000",
            "0.00004,   0.000000",
            "0.00005,   0.000001",
            "19.999999, 0.200000"
    })
    void deberiaDividirEntreCienYConvertirPorcentajeADecimal(String porcentaje, String esperado) {
        // ACT
        BigDecimal resultado = matematicaFinanciera.dividirEntreCien(new BigDecimal(porcentaje));
        // ASSERT
        assertEquals(new BigDecimal(esperado), resultado);
    }

    @ParameterizedTest
    @CsvSource({
            "true,  50000,     15,       7500.000000",
            "true,  13550.123, 12.1234,  1642.735612",
            "true,  10000,     33.33334, 3333.330000",
            "false, 50000,     20,       0.000000"
    })
    void deberiaCalcularMontoDescuentoCorrectamente(
            boolean activo, String precioBase, String porcentaje, String esperado
    ) {
        // ARRANGE
        Descuento descuentoFalso = Mockito.mock(Descuento.class);
        when(descuentoFalso.isActivo()).thenReturn(activo);
        if (activo) {
            when(descuentoFalso.getPorcentaje()).thenReturn(new BigDecimal(porcentaje));
        }
        // ACT
        BigDecimal resultado = matematicaFinanciera.calcularMontoDescuento(
                new BigDecimal(precioBase), descuentoFalso
        );
        // ASSERT
        assertEquals(new BigDecimal(esperado), resultado);
    }

    @ParameterizedTest
    @CsvSource({
            "true,  150000,     19,      28500.000000",
            "true,  45500.555,  19.9999, 9100.065499",
            "true,  12345.67,   5,       617.283500",
            "false, 150000,     19,      0.000000"
    })
    void deberiaCalcularMontoImpuestoCorrectamente(
            boolean activo, String valorSinImpuesto, String porcentaje, String esperado
    ) {
        // ARRANGE
        Impuesto impuestoFalso = Mockito.mock(Impuesto.class);
        when(impuestoFalso.isActivo()).thenReturn(activo);
        if (activo) {
            when(impuestoFalso.getPorcentaje()).thenReturn(new BigDecimal(porcentaje));
        }
        // ACT
        BigDecimal resultado = matematicaFinanciera.calcularMontoImpuesto(
                new BigDecimal(valorSinImpuesto), impuestoFalso
        );
        // ASSERT
        assertEquals(new BigDecimal(esperado), resultado);
    }

}//===================================================================================================================//

