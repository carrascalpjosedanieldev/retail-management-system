package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ItemFacturable {

    TipoItem getTipoItem();

    String getNombre();

    String getCodigo();

    BigDecimal getPorcentajeImpuesto();

    BigDecimal calcularImpuesto(BigDecimal precioFinalSinImpuesto);

    BigDecimal getPorcentajeDescuento();

    BigDecimal calcularDescuento(BigDecimal valorVenta);

    BigDecimal getValorFinalSinImpuesto(LocalDate fecha);

    BigDecimal getValorVenta(LocalDate fecha);

}//===================================================================================================================//

