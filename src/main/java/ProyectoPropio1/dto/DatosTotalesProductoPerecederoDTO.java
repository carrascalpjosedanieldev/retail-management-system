package ProyectoPropio1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DatosTotalesProductoPerecederoDTO(
        String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
        BigDecimal valorVentaFinal, int stock, ImpuestoDTO datosImpuesto, DescuentoDTO datosDescuento,
        LocalDate fechaVencimiento, PoliticaVencimientoDTO datosPoliticaVencimiento, String estaVencido,
        String disponible
) implements DatosTotalesProductoDTO{ }

