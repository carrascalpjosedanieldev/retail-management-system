package ProyectoPropio1.dto;

import java.math.BigDecimal;

public record ItemCarritoDTO(String nombreArticulo, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
}

