package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.PoliticaVencimiento;

import java.util.List;

public interface RepositorioPoliticaVencimiento {

    //CREATE:

    void insertarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento);

    //READ

    PoliticaVencimiento obtenerPoliticaVencimiento(int idPoliticaVencimiento);

    List<PoliticaVencimiento> obtenerPoliticasVencimientoActivas();

    List<PoliticaVencimiento> obtenerPoliticasVencimientoInactivas();

    //UPDATE:

    void actualizarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento);

}//===================================================================================================================//

