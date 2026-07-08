package ProyectoPropio1.dominio.puertos;

public interface RepositorioConfiguracion {

    String obtenerValorConfiguracion(String clave);

    void actualizarValorConfiguracion(String clave, String valor, String descripcion);

    String obtenerDescripcionConfiguracion(String clave);

}
