package RetailManagementSystem.aplicacion.dto.comercial;

import java.math.BigDecimal;

public record ServicioDTO(String codigo, String nombre, BigDecimal precioBase, BigDecimal precioFinal, String estado,
                          int idImpuesto, String nombreImpuesto,
                          int idDescuento, String nombreDescuento) {
}

