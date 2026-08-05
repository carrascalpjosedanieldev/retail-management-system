package RetailManagementSystem.aplicacion.dto.comercial;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.dominio.enums.Talla;

import java.math.BigDecimal;

public record DatosTotalesProductoRopaDTO(
        String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
        BigDecimal valorVentaFinal, int stock, ImpuestoDTO datosImpuesto, DescuentoDTO datosDescuento,
        Talla talla, String disponible
) implements DatosTotalesProductoDTO{ }

