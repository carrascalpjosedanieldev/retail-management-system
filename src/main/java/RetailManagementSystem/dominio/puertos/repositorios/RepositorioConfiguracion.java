package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.aplicacion.dto.consultas.ConfiguracionSistemaDTO;

public interface RepositorioConfiguracion {

    //READ:

    String obtenerValorConfiguracion(String clave);

    ConfiguracionSistemaDTO obtenerConfiguracionSistema(String clave);

    //UPDATE:

    void actualizarValorConfiguracion(String clave, String valor);

    void actualizarConfiguracionSistemaConfiguracion(String clave, String valor, String descripcion);

}//===================================================================================================================//

