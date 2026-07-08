package ProyectoPropio1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DatosVentaProductoPerecederoDTO(String nombre, BigDecimal precio, LocalDate fechaVencimiento) implements DatosVentaProductoDTO {
    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public BigDecimal precio() {
        return precio;
    }
}

