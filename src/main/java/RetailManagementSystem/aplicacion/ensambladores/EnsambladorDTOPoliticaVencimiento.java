package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOPoliticaVencimiento {

    //CONSTRUCTOR:

    public EnsambladorDTOPoliticaVencimiento() {
    }

    //MÉTODOS:

    public PoliticaVencimientoDTO ensamblarDatosPoliticaVencimiento(PoliticaVencimiento politicaVencimiento){
        if (politicaVencimiento == null){
            throw new IllegalArgumentException("NO puedes ensamblar un DTO de una Política de Vencimiento Vacía.");
        }
        return new PoliticaVencimientoDTO(
                politicaVencimiento.getIdPolitica(), politicaVencimiento.getNombre(),
                politicaVencimiento.getDiasUmbral(), politicaVencimiento.getPorcentajeDescuento(),
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

