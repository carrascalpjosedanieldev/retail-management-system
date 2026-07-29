package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.ResumenVentaDiaDTO;
import RetailManagementSystem.dominio.entidades.Factura;
import RetailManagementSystem.dominio.entidades.ItemVendido;
import RetailManagementSystem.dominio.entidades.ReporteRecaudo;
import RetailManagementSystem.aplicacion.dto.ItemVendidoFacturaDTO;
import RetailManagementSystem.aplicacion.dto.FacturaDTO;
import RetailManagementSystem.aplicacion.dto.ReporteRecaudoDTO;
import RetailManagementSystem.dominio.entidades.ResumenVentaDia;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOFactura {

    public EnsambladorDTOFactura(){
    }

    public FacturaDTO ensamblarFactura(Factura factura){
        List<ItemVendidoFacturaDTO> datosItemsFactura = new ArrayList<>();
        for (ItemVendido itemVendido : factura.getItemsFinales()){
            ItemVendidoFacturaDTO datosItem = new ItemVendidoFacturaDTO(itemVendido.getTipoItem().name(),
                    itemVendido.getCodigo(), itemVendido.getNombre(), itemVendido.getCantidad(), itemVendido.getPrecioUnitario(),
                    itemVendido.getSubtotalNeto(), itemVendido.getPorcentajeImpuesto(), itemVendido.getMontoImpuesto(),
                    itemVendido.getTotalLinea());
            datosItemsFactura.add(datosItem);
        }
        return new FacturaDTO(factura.getNumeroFactura(), datosItemsFactura, factura.getFechaHoraEmision(), factura.getSubTotal(),
                factura.getTotalImpuestos(), factura.getTotalGeneral());
    }

    public ReporteRecaudoDTO ensamblarReporteRecaudo(ReporteRecaudo reporteRecaudo){
        return new ReporteRecaudoDTO(
                reporteRecaudo.getFechaInicio(), reporteRecaudo.getFechaFin(),
                reporteRecaudo.getCantidadFacturasEmitidas(), reporteRecaudo.getSubTotal(),
                reporteRecaudo.getTotalImpuestos(), reporteRecaudo.getTotalRecaudo()
        );
    }

    public ResumenVentaDiaDTO ensamblarResumenVentaDia(ResumenVentaDia resumenVentaDia){
        return new ResumenVentaDiaDTO(
                resumenVentaDia.getTotalVentas(), resumenVentaDia.getCantidadFacturas(),
                resumenVentaDia.getUltimaVenta()
        );
    }

}//===================================================================================================================//

