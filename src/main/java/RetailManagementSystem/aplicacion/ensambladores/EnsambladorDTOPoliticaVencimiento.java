package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.PoliticaVencimiento;
import RetailManagementSystem.aplicacion.dto.PoliticaVencimientoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOPoliticaVencimiento {

    public EnsambladorDTOPoliticaVencimiento() {
    }

    public PoliticaVencimientoDTO ensamblarDatosPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        String estado;
        if (politicaVencimiento.isActiva()){
            estado = "Activo";
        } else {
            estado = "Inactivo";
        }
        return new PoliticaVencimientoDTO(
                politicaVencimiento.getIdPolitica(), politicaVencimiento.getNombre(),
                politicaVencimiento.getDiasUmbral(), politicaVencimiento.getPorcentajeDescuento(), estado
        );
    }

    public List<PoliticaVencimientoDTO> ensamblarDetallePoliticasVencimiento(List<PoliticaVencimiento> politicasVencimiento){
        List<PoliticaVencimientoDTO> detallePoliticasVencimientoActivas = new ArrayList<>();
        for (PoliticaVencimiento politicaVencimiento:politicasVencimiento){
            PoliticaVencimientoDTO datosPoliticaVencimiento = this.ensamblarDatosPoliticaVencimiento(politicaVencimiento);
            detallePoliticasVencimientoActivas.add(datosPoliticaVencimiento);
        }
        return detallePoliticasVencimientoActivas;
    }

}
