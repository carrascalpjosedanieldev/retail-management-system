package RetailManagementSystem.aplicacion.dto.comercial;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DatosTotalesProductoPerecederoDTO(
        String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia,
        BigDecimal valorVentaFinal, int stock, ImpuestoDTO datosImpuesto, DescuentoDTO datosDescuento,
        LocalDate fechaVencimiento, PoliticaVencimientoDTO datosPoliticaVencimiento, boolean estaVencido,
        boolean activo
) implements DatosTotalesProductoDTO{ }

