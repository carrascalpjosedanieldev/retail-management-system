package RetailManagementSystem.aplicacion.dto.gestion;

import java.math.BigDecimal;

public record DescuentoDTO(int idDescuento, String nombre, BigDecimal porcentaje, String estado) {
}

