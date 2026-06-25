package ProyectoPropio1.servicios;

import ProyectoPropio1.dominio.Factura;
import ProyectoPropio1.dto.FacturaDTO;
import ProyectoPropio1.dto.HistorialVentasDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOHistorialFacturas {

    private final EnsambladorDTOFactura ensambladorDTOFactura;

    public EnsambladorDTOHistorialFacturas(EnsambladorDTOFactura ensambladorDTOFactura) {
        this.ensambladorDTOFactura = ensambladorDTOFactura;
    }

    public HistorialVentasDTO ensamblarHistorialFactura(List<Factura> facturasProcesadas){
        List<FacturaDTO> facturasRegistradas = new ArrayList<>();
        double totalRecaudo = 0;
        for (Factura factura:facturasProcesadas){
            FacturaDTO facturaDTO = this.ensambladorDTOFactura.ensamblarFactura(factura);
            facturasRegistradas.add(facturaDTO);
            totalRecaudo += facturaDTO.pagoTotal();
        }
        return new HistorialVentasDTO(facturasRegistradas, totalRecaudo);
    }

}

