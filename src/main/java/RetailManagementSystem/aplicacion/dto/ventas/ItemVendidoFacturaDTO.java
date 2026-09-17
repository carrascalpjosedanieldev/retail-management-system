package RetailManagementSystem.aplicacion.dto.ventas;

import RetailManagementSystem.dominio.enums.TipoItem;

import java.math.BigDecimal;

public record ItemVendidoFacturaDTO(
        TipoItem tipoItem, String codigoReferencia, String nombreItem, int cantidad, BigDecimal precioUnitario,
        BigDecimal subTotalNeto, BigDecimal porcentajeImpuestos, BigDecimal montoImpuestos, BigDecimal totalLinea
) { }

