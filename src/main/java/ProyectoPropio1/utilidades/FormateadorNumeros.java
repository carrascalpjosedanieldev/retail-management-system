package ProyectoPropio1.utilidades;

import java.math.BigDecimal;

public class FormateadorNumeros {

    public static BigDecimal stringABigDecimal(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new NumberFormatException("El campo de valor no puede estar vacío.");
        }
        try {
            String valorLimpio = valor.trim().replace(",", ".");
            return new BigDecimal(valorLimpio);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Formato numérico inválido. Usa solo números.");
        }
    }

}
