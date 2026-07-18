package ProyectoPropio1.dto;

import ProyectoPropio1.dominio.enums.Talla;

import java.math.BigDecimal;

public record DatosTotalesProductoRopaDTO(
        String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
        BigDecimal valorVentaFinal, int stock, ImpuestoDTO datosImpuesto, DescuentoDTO datosDescuento,
        Talla talla, String disponible
) implements DatosTotalesProductoDTO{ }

