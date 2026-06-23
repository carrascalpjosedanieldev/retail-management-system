package ProyectoPropio1.dto;

import java.time.LocalDate;

public record DatosTotalesProductoPerecederoDTO(int codigo, String nombre, double valorCompra, double porcentajeGanancia, double valorVentaBase, int stock, LocalDate fechaVencimiento, boolean estaVencido) implements DatosTotalesProductoDTO{
}

