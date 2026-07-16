package ProyectoPropio1.utilidades;

import ProyectoPropio1.servicios.ensambladores.EnsambladorDTODescuento;

public class FabricaEnsambladores {

    private static EnsambladorDTODescuento ensambladorDTODescuento;

    public static EnsambladorDTODescuento obtenerEnsambladorDTODescuento(){
        if (ensambladorDTODescuento==null){
            ensambladorDTODescuento = new EnsambladorDTODescuento();
        }
        return ensambladorDTODescuento;
    }

}
