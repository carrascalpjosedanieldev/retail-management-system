package ProyectoPropio1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DatosTotalesProductoPerecederoDTO(String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, BigDecimal valorVentaBase, int stock, LocalDate fechaVencimiento, String estaVencido) implements DatosTotalesProductoDTO{
}

