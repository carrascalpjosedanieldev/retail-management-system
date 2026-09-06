package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ItemFacturable {

    TipoItem getTipoItem();

    String getNombre();

    String getCodigo();

    Impuesto getImpuesto();

    BigDecimal calcularImpuesto(BigDecimal precioFinalSinImpuesto);

    BigDecimal calcularDescuento(BigDecimal valorVenta);

    BigDecimal getValorFinalSinImpuesto(LocalDate fecha);

    BigDecimal getValorVenta(LocalDate fecha);

}//===================================================================================================================//

