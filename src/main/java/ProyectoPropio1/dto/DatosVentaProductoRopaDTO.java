package ProyectoPropio1.dto;

import ProyectoPropio1.dominio.enums.Talla;

import java.math.BigDecimal;

public record DatosVentaProductoRopaDTO(String nombre, BigDecimal precio, Talla talla) implements DatosVentaProductoDTO {
    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public BigDecimal precio() {
        return precio;
    }
}

