package ProyectoPropio1.utilidades;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class FormateadorNumeros {

    private static final Locale LOCALIDAD_SISTEMA = Locale.of("es", "CO");

    private static void validarVacio(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo no puede estar vacío.");
        }
    }

    public static BigDecimal stringAPrecio(String valor) {
        validarVacio(valor);
        try {
            NumberFormat formato = NumberFormat.getInstance(LOCALIDAD_SISTEMA);
            if (formato instanceof DecimalFormat) {
                ((DecimalFormat) formato).setParseBigDecimal(true);
            }
            BigDecimal precio = (BigDecimal) formato.parse(valor.trim());
            if (precio.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El precio no puede ser un valor negativo.");
            }
            return precio;
        } catch (ParseException e) {
            throw new NumberFormatException("Formato de precio inválido. Usa puntos para miles y coma para decimales (Ej: 1.500,50).");
        }
    }

    public static BigDecimal stringAPorcentaje(String valor) {
        validarVacio(valor);
        String valorLimpio = valor.trim().replace(",", ".");
        try {
            BigDecimal porcentaje = new BigDecimal(valorLimpio);
            if (porcentaje.compareTo(BigDecimal.ZERO) < 0 || porcentaje.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException("El Porcentaje debe estar entre 0 y 100.");
            }
            return porcentaje;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Formato numérico inválido. Ingresa un porcentaje válido (Ej: 15 o 15.5).");
        }
    }

}

