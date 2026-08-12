package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOImpuesto {

    public EnsambladorDTOImpuesto() {
    }

    public ImpuestoDTO ensamblarDatosImpuesto(Impuesto impuesto){
        return new ImpuestoDTO(
                impuesto.getId(), impuesto.getNombre(), impuesto.getPorcentaje(), impuesto.isActivo()
        );
    }

    public List<ImpuestoDTO> ensamblarDetalleImpuestos(List<Impuesto> impuestos){
        List<ImpuestoDTO> detalleImpuestosActivos = new ArrayList<>();
        String estado;
        for (Impuesto impuesto:impuestos){
            ImpuestoDTO datosImpuesto = this.ensamblarDatosImpuesto(impuesto);
            detalleImpuestosActivos.add(datosImpuesto);
        }
        return detalleImpuestosActivos;
    }

}//===================================================================================================================//

