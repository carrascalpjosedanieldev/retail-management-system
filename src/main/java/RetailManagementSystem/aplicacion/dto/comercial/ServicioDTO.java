package RetailManagementSystem.aplicacion.dto.comercial;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;

import java.math.BigDecimal;

public record ServicioDTO(
        String codigo, String nombre, BigDecimal precioBase, BigDecimal precioFinal, boolean activo,
        ImpuestoDTO datosImpuesto, DescuentoDTO datosDescuento
) { }

