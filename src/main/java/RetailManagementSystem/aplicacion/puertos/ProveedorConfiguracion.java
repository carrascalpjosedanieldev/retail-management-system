package RetailManagementSystem.aplicacion.puertos;

public interface ProveedorConfiguracion {

    String obtenerValorConfiguracion(String clave);

    void invalidarCache(String clave);

    void invalidarCacheCompleto();

}//===================================================================================================================//

