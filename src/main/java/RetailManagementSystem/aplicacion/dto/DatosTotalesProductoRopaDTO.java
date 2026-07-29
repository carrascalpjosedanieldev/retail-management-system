package RetailManagementSystem.aplicacion.dto;

import RetailManagementSystem.dominio.enums.Talla;

import java.math.BigDecimal;

public record DatosTotalesProductoRopaDTO(
        String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
        BigDecimal valorVentaFinal, int stock, ImpuestoDTO datosImpuesto, DescuentoDTO datosDescuento,
        Talla talla, String disponible
) implements DatosTotalesProductoDTO{ }

