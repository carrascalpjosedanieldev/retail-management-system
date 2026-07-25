package ProyectoPropio1.servicios.aplicacion.ensambladores;

import ProyectoPropio1.dominio.Impuesto;
import ProyectoPropio1.dto.ImpuestoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOImpuesto {

    public EnsambladorDTOImpuesto() {
    }

    public ImpuestoDTO ensamblarDatosImpuesto(Impuesto impuesto){
        String estado;
        if (impuesto.isActivo()){
            estado = "Activo";
        } else {
            estado = "Inactivo";
        }
        return new ImpuestoDTO(
                impuesto.getId(), impuesto.getNombre(), impuesto.getPorcentaje(), estado
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

}
