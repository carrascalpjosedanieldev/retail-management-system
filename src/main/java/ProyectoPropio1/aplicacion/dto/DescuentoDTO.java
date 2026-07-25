package ProyectoPropio1.aplicacion.dto;

import java.math.BigDecimal;

public record DescuentoDTO(int idDescuento, String nombre, BigDecimal porcentaje, String estado) {
}

