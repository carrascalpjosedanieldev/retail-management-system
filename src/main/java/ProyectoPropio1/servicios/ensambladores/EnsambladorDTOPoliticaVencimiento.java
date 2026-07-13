package ProyectoPropio1.servicios.ensambladores;

import ProyectoPropio1.dominio.PoliticaVencimiento;
import ProyectoPropio1.dto.PoliticaVencimientoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOPoliticaVencimiento {

    public EnsambladorDTOPoliticaVencimiento() {
    }

    public List<PoliticaVencimientoDTO> ensamblarDetallePoliticasVencimiento(List<PoliticaVencimiento> politicasVencimiento){
        List<PoliticaVencimientoDTO> detallePoliticasVencimientoActivas = new ArrayList<>();
        String estado;
        for (PoliticaVencimiento politicaVencimiento:politicasVencimiento){
            if (politicaVencimiento.isActiva()){
                estado = "Activo";
            } else {
                estado = "Inactivo";
            }
            PoliticaVencimientoDTO politicaVencimientoDTO = new PoliticaVencimientoDTO(
                    politicaVencimiento.getIdPolitica(), politicaVencimiento.getNombre(),
                    politicaVencimiento.getDiasUmbral(), politicaVencimiento.getPorcentajeDescuento(), estado
            );
            detallePoliticasVencimientoActivas.add(politicaVencimientoDTO);
        }
        return detallePoliticasVencimientoActivas;
    }

}
