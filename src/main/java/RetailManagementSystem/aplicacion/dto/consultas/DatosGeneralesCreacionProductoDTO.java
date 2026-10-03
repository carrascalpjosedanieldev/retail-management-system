package RetailManagementSystem.aplicacion.dto.consultas;

import RetailManagementSystem.dominio.enums.TipoProducto;

import java.math.BigDecimal;

public record DatosGeneralesCreacionProductoDTO(
        TipoProducto tipoProducto, String nombre, BigDecimal valorCompra, BigDecimal ganancia, int stock,
        int idImpuesto, int idDescuento
) { }

