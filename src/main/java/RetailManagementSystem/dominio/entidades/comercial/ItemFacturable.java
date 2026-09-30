package RetailManagementSystem.dominio.entidades.comercial;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.TipoItem;

public interface ItemFacturable {

    TipoItem getTipoItem();

    String getNombre();

    String getCodigo();

    Impuesto getImpuesto();

}//===================================================================================================================//

