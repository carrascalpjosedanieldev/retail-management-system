package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.ventas.Factura;
import RetailManagementSystem.dominio.entidades.ventas.ItemVendido;
import RetailManagementSystem.aplicacion.dto.ventas.ItemVendidoFacturaDTO;
import RetailManagementSystem.aplicacion.dto.ventas.FacturaDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOFactura {

    //CONSTRUCTOR:

    public EnsambladorDTOFactura(){
    }

    //MÉTODOS:

    public FacturaDTO ensamblarFactura(Factura factura){
        if (factura == null){
            throw new IllegalArgumentException("NO puedes ensamblar un DTO de una Factura Vacía.");
        }
        List<ItemVendidoFacturaDTO> datosItemsFactura = new ArrayList<>();
        for (ItemVendido itemVendido : factura.getItemsFinales()){
            ItemVendidoFacturaDTO datosItem = new ItemVendidoFacturaDTO(
                    itemVendido.getTipoItem(),
                    itemVendido.getCodigo(),
                    itemVendido.getNombre(),
                    itemVendido.getCantidad(),
                    itemVendido.getPrecioUnitario(),
                    itemVendido.getSubtotalNeto(),
                    itemVendido.getPorcentajeImpuesto(),
                    itemVendido.getMontoImpuesto(),
                    itemVendido.getTotalLinea()
            );
            datosItemsFactura.add(datosItem);
        }
        return new FacturaDTO(
                factura.getNumeroFactura(), datosItemsFactura, factura.getFechaHoraEmision(), factura.getSubTotal(),
                factura.getTotalImpuestos(), factura.getTotalGeneral()
        );
    }

}//===================================================================================================================//

