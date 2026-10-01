package RetailManagementSystem.aplicacion.ensambladores.gestion;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOPoliticaVencimiento {

    //CONSTRUCTOR:

    public EnsambladorDTOPoliticaVencimiento() { }

    //MÉTODOS:

    public PoliticaVencimientoDTO ensamblarDatosPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        return new PoliticaVencimientoDTO(
                politicaVencimiento.getIdPolitica(), politicaVencimiento.getNombre(),
                politicaVencimiento.getDiasUmbral(),
                politicaVencimiento.getPorcentajeDescuento().setScale(2, RoundingMode.HALF_UP),
                politicaVencimiento.isActiva()
        );
    }

    public List<PoliticaVencimientoDTO> ensamblarDetallePoliticasVencimiento(
            List<PoliticaVencimiento> politicasVencimiento
    ) {
        List<PoliticaVencimientoDTO> detallePoliticasVencimientoActivas = new ArrayList<>();
        for (PoliticaVencimiento politicaVencimiento:politicasVencimiento){
            PoliticaVencimientoDTO datosPoliticaVencimiento = ensamblarDatosPoliticaVencimiento(politicaVencimiento);
            detallePoliticasVencimientoActivas.add(datosPoliticaVencimiento);
        }
        return detallePoliticasVencimientoActivas;
    }

}//===================================================================================================================//

