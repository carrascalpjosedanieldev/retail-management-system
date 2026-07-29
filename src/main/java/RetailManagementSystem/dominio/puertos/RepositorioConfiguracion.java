package RetailManagementSystem.dominio.puertos;

public interface RepositorioConfiguracion {

    String obtenerValorConfiguracion(String clave);

    void actualizarValorConfiguracion(String clave, String valor);

    void actualizarDescripcionConfiguracion(String clave, String descripcion);

    void actualizarValorYDescripcionConfiguracion(String clave, String valor, String descripcion);

    String obtenerDescripcionConfiguracion(String clave);

}
