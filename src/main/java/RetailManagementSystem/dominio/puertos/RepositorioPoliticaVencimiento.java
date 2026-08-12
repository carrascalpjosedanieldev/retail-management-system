package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;

import java.util.List;

public interface RepositorioPoliticaVencimiento {

    //CREATE:

    PoliticaVencimiento insertarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento);

    //READ

    PoliticaVencimiento obtenerPoliticaVencimiento(int idPoliticaVencimiento);

    List<PoliticaVencimiento> obtenerPoliticasVencimientoActivas();

    List<PoliticaVencimiento> obtenerTodasLasPoliticasDeVencimiento();

    //UPDATE:

    void actualizarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento);

}//===================================================================================================================//

