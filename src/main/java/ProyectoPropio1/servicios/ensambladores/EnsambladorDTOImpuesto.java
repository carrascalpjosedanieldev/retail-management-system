package ProyectoPropio1.servicios.ensambladores;

import ProyectoPropio1.dominio.Impuesto;
import ProyectoPropio1.dto.ImpuestoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOImpuesto {

    public EnsambladorDTOImpuesto() {
    }

    public List<ImpuestoDTO> ensamblarDetalleImpuestos(List<Impuesto> impuestos){
        List<ImpuestoDTO> detalleImpuestosActivos = new ArrayList<>();
        String estado;
        for (Impuesto impuesto:impuestos){
            if (impuesto.isActivo()){
                estado = "Activo";
            } else {
                estado = "Inactivo";
            }
            ImpuestoDTO impuestoDTO = new ImpuestoDTO(impuesto.getId(), impuesto.getNombre(), impuesto.getPorcentaje(), estado);
            detalleImpuestosActivos.add(impuestoDTO);
        }
        return detalleImpuestosActivos;
    }

}
