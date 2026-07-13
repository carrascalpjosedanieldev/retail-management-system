package ProyectoPropio1.dto;

import java.math.BigDecimal;

public record PoliticaVencimientoDTO(int idPoliticaVencimiento, String nombrePolitica, int diasUmbral,
                                     BigDecimal porcentajeDescuento, String estado) {
}
