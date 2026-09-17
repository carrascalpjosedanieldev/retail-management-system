package RetailManagementSystem.aplicacion.dto.ventas;

import RetailManagementSystem.dominio.enums.TipoItem;

import java.math.BigDecimal;

public record ItemCarritoDTO(
        String codigoArticulo, TipoItem tipoItem, String nombreArticulo, int cantidad, BigDecimal precioUnitario,
        BigDecimal subtotal, BigDecimal impuestos
) { }

