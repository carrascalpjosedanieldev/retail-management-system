package ProyectoPropio1;

import java.time.LocalDate;

public record DatosVentaProductoPerecederoDTO(String nombre, double precio, LocalDate fechaVencimiento) implements DatosVentaProductoDTO {
    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public double precio() {
        return precio;
    }
}

