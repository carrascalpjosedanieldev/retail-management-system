package ProyectoPropio1.utilidades;

import ProyectoPropio1.servicios.ensambladores.EnsambladorDTODescuento;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOImpuesto;

public class FabricaEnsambladores {

    private static EnsambladorDTODescuento ensambladorDTODescuento;

    public static EnsambladorDTODescuento obtenerEnsambladorDTODescuento(){
        if (ensambladorDTODescuento == null){
            ensambladorDTODescuento = new EnsambladorDTODescuento();
        }
        return ensambladorDTODescuento;
    }

    private static EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    public static EnsambladorDTOImpuesto obtenerEnsambladorDTOImpuesto(){
        if (ensambladorDTOImpuesto == null){
            ensambladorDTOImpuesto = new EnsambladorDTOImpuesto();
        }
        return ensambladorDTOImpuesto;
    }

}
