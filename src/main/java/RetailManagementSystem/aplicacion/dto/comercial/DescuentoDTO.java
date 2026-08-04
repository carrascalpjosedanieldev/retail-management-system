package RetailManagementSystem.aplicacion.dto.comercial;

import java.math.BigDecimal;

public record DescuentoDTO(int idDescuento, String nombre, BigDecimal porcentaje, String estado) {
}

