package RetailManagementSystem.aplicacion.ensambladores.gestion;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOImpuesto {

    //CONSTRUCTOR:

    public EnsambladorDTOImpuesto() { }

    //MÉTODOS:

    public ImpuestoDTO ensamblarDatosImpuesto(Impuesto impuesto){
        return new ImpuestoDTO(
                impuesto.getId(), impuesto.getNombre(),
                impuesto.getPorcentaje().setScale(2, RoundingMode.HALF_UP), impuesto.isActivo()
        );
    }

    public List<ImpuestoDTO> ensamblarDetalleImpuestos(List<Impuesto> impuestos){
        List<ImpuestoDTO> detalleImpuestosActivos = new ArrayList<>();
        for (Impuesto impuesto:impuestos){
            ImpuestoDTO datosImpuesto = this.ensamblarDatosImpuesto(impuesto);
            detalleImpuestosActivos.add(datosImpuesto);
        }
        return detalleImpuestosActivos;
    }

}//===================================================================================================================//

