package RetailManagementSystem.dominio.puertos;

public interface RepositorioConfiguracion {

    //READ:

    String obtenerValorConfiguracion(String clave);

    String obtenerDescripcionConfiguracion(String clave);

    //UPDATE:

    void actualizarValorConfiguracion(String clave, String valor);

    void actualizarDescripcionConfiguracion(String clave, String descripcion);

    void actualizarValorYDescripcionConfiguracion(String clave, String valor, String descripcion);

}//===================================================================================================================//

