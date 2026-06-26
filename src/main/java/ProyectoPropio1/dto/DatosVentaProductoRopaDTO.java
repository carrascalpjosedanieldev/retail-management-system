package ProyectoPropio1.dto;

import ProyectoPropio1.dominio.Talla;

public record DatosVentaProductoRopaDTO(String nombre, double precio, Talla talla) implements DatosVentaProductoDTO {
    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public double precio() {
        return precio;
    }
}

