package ProyectoPropio1.utilidades;

import ProyectoPropio1.servicios.ensambladores.*;

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

    private static EnsambladorDTOServicio ensambladorDTOServicio;

    public static EnsambladorDTOServicio obtenerEnsambladorDTOServicio(){
        if (ensambladorDTOServicio == null){
            ensambladorDTOServicio = new EnsambladorDTOServicio();
        }
        return ensambladorDTOServicio;
    }

    private static EnsambladorDTOInventario ensambladorDTOInventario;

    public static EnsambladorDTOInventario obtenerEnsambladorDTOInventario(){
        if (ensambladorDTOInventario == null){
            ensambladorDTOInventario = new EnsambladorDTOInventario(obtenerEnsambladorDTOProducto());
        }
        return ensambladorDTOInventario;
    }

    private static EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;

    public static EnsambladorDTOPoliticaVencimiento obtenerEnsambladorDTOPoliticaVencimiento(){
        if (ensambladorDTOPoliticaVencimiento == null){
            ensambladorDTOPoliticaVencimiento = new EnsambladorDTOPoliticaVencimiento();
        }
        return ensambladorDTOPoliticaVencimiento;
    }

    private static EnsambladorDTOProducto ensambladorDTOProducto;

    public static EnsambladorDTOProducto obtenerEnsambladorDTOProducto(){
        if (ensambladorDTOProducto == null){
            EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento = new EnsambladorDTOPoliticaVencimiento();
            ensambladorDTOProducto = new EnsambladorDTOProducto(
                    obtenerEnsambladorDTOImpuesto(), obtenerEnsambladorDTODescuento(),
                    obtenerEnsambladorDTOPoliticaVencimiento());
        }
        return ensambladorDTOProducto;
    }

}//===================================================================================================================//

