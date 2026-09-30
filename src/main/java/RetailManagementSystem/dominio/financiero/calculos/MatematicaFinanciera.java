package RetailManagementSystem.dominio.financiero.calculos;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MatematicaFinanciera {

    //ATRIBUTOS:

    public static final int ESCALA_CALCULO = 6;

    public static final RoundingMode REDONDEO_ESTANDAR = RoundingMode.HALF_UP;

    public static final BigDecimal CIEN = new BigDecimal("100");

    //CONSTRUCTOR:

    public MatematicaFinanciera() { }

    //MÉTODOS:

    public BigDecimal aplicarEscala(BigDecimal valor) {
        return valor.setScale(ESCALA_CALCULO, REDONDEO_ESTANDAR);
    }

    public BigDecimal dividirEntreCien(BigDecimal porcentaje) {
        return porcentaje.divide(CIEN, ESCALA_CALCULO, REDONDEO_ESTANDAR);
    }

    public BigDecimal calcularMontoDescuento(BigDecimal precioBase, Descuento descuento) {
        if (!descuento.isActivo()) {
            return aplicarEscala(BigDecimal.ZERO);
        }
        return aplicarEscala(precioBase.multiply(dividirEntreCien(descuento.getPorcentaje())));
    }

    public BigDecimal calcularMontoImpuesto(BigDecimal valorSinImpuesto, Impuesto impuesto) {
        if (!impuesto.isActivo()) {
            return aplicarEscala(BigDecimal.ZERO);
        }
        return aplicarEscala(valorSinImpuesto.multiply(dividirEntreCien(impuesto.getPorcentaje())));
    }

}//===================================================================================================================//

