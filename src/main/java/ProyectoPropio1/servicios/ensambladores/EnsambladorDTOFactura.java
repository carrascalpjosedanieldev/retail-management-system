package ProyectoPropio1.servicios.ensambladores;

import ProyectoPropio1.dominio.Factura;
import ProyectoPropio1.dominio.ItemVendido;
import ProyectoPropio1.dto.DatosItemVendidoFacturaDTO;
import ProyectoPropio1.dto.FacturaDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOFactura {

    public EnsambladorDTOFactura(){
    }

    public FacturaDTO ensamblarFactura(Factura factura){
        List<DatosItemVendidoFacturaDTO> datosItemsFactura = new ArrayList<>();
        for (ItemVendido itemVendido : factura.getItemsFinales()){
            DatosItemVendidoFacturaDTO datosItem = new DatosItemVendidoFacturaDTO(itemVendido.getTipoItem().name(),
                    itemVendido.getCodigo(), itemVendido.getNombre(), itemVendido.getCantidad(), itemVendido.getPrecioUnitario(),
                    itemVendido.getSubtotalNeto(), itemVendido.getPorcentajeImpuesto(), itemVendido.getMontoImpuesto(),
                    itemVendido.getTotalLinea());
            datosItemsFactura.add(datosItem);
        }
        return new FacturaDTO(factura.getNumeroFactura(), datosItemsFactura, factura.getFechaHoraEmision(), factura.getSubTotal(),
                factura.getTotalImpuestos(), factura.getTotalGeneral());
    }

}

