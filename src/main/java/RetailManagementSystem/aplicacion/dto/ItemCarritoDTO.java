package RetailManagementSystem.aplicacion.dto;

import java.math.BigDecimal;

public record ItemCarritoDTO(
        String codigoArticulo, String tipoItem, String nombreArticulo, int cantidad, BigDecimal precioUnitario,
        BigDecimal subtotal, BigDecimal impuestos
) { }

