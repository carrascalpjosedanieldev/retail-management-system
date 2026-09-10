package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.aplicacion.dto.consultas.ReporteRecaudoDTO;
import RetailManagementSystem.dominio.entidades.ventas.Factura;
import RetailManagementSystem.dominio.entidades.ventas.ItemVendido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RepositorioFacturas {

    //CREATE:

    Factura insertarFactura(List<ItemVendido> items);

    //READ:

    ReporteRecaudoDTO obtenerReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin);

    BigDecimal obtenerTotalUltimaVenta(LocalDate fecha);

}//===================================================================================================================//

