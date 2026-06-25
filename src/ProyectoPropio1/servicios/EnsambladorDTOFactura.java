package ProyectoPropio1.servicios;

import ProyectoPropio1.dominio.Factura;
import ProyectoPropio1.dominio.ItemFacturable;
import ProyectoPropio1.dto.DatosLineaFacturaDTO;
import ProyectoPropio1.dto.FacturaDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOFactura {

    public EnsambladorDTOFactura(){
    }

    public FacturaDTO ensamblarFactura(Factura factura){
        List<DatosLineaFacturaDTO> datosLineaFactura = new ArrayList<>();
        for (ItemFacturable itemFacturable: factura.getItemsFinales()){
            datosLineaFactura.add(new DatosLineaFacturaDTO(itemFacturable.getTipoItem(), itemFacturable.getNombre(), itemFacturable.getCantidad(), itemFacturable.getValorCobrado()));
        }
        return new FacturaDTO(factura.getIdFactura(), datosLineaFactura, factura.calcularTotalFactura());
    }

}

