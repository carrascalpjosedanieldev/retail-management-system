package ProyectoPropio1.aplicacion.dto;

import java.math.BigDecimal;

public record ItemCarritoDTO(String nombreArticulo, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
}

