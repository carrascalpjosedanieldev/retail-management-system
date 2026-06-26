package ProyectoPropio1.dto;

import ProyectoPropio1.dominio.Talla;

public record DatosTotalesProductoRopaDTO(int codigo, String nombre, double valorCompra, double porcentajeGanancia, double valorVentaBase, int stock, Talla talla) implements DatosTotalesProductoDTO{
}

