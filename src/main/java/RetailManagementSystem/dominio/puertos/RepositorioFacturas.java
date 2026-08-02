package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.ventas.Factura;
import RetailManagementSystem.dominio.entidades.ventas.ItemVendido;
import RetailManagementSystem.dominio.entidades.ventas.ReporteRecaudo;

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

