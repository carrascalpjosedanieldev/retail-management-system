package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOImpuesto {

    //CONSTRUCTOR:

    public EnsambladorDTOImpuesto() {
    }

    //MÉTODOS:

    public ImpuestoDTO ensamblarDatosImpuesto(Impuesto impuesto){
        if (impuesto == null){
            throw new IllegalArgumentException("NO puedes ensamblar un DTO de un Impuesto Vacío.");
        }
        return new ImpuestoDTO(
                impuesto.getId(), impuesto.getNombre(), impuesto.getPorcentaje(), impuesto.isActivo()
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

