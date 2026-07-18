package ProyectoPropio1.dto;

import java.math.BigDecimal;

public record ProductoResumenDTO (
        String codigoProducto, String nombre, BigDecimal valorVenta, int stock, boolean disponible
) { }

