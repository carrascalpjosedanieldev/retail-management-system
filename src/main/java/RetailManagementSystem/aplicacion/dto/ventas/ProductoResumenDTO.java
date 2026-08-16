package RetailManagementSystem.aplicacion.dto.ventas;

import java.math.BigDecimal;

public record ProductoResumenDTO (
        String codigoProducto, String nombre, BigDecimal valorVenta, int stock, boolean activo
) { }

