package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.Factura;
import RetailManagementSystem.dominio.entidades.ItemVendido;
import RetailManagementSystem.dominio.entidades.ReporteRecaudo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RepositorioFacturas {

    //CREATE:

    Factura insertarFactura(List<ItemVendido> items);

    //READ:

    ReporteRecaudo obtenerReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin);

    BigDecimal obtenerTotalUltimaVenta(LocalDate fecha);

}//===================================================================================================================//

