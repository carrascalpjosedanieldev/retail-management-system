package RetailManagementSystem.infraestructura.persistencia.mysql.repositorios;

import RetailManagementSystem.aplicacion.dto.consultas.ConfiguracionSistemaDTO;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioConfiguracion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RepositorioConfiguracionCacheado implements RepositorioConfiguracion {

    //ATRIBUTOS:

    private final RepositorioConfiguracion repositorioBD;

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    private final Map<String, ConfiguracionSistemaDTO> cacheSistemas = new ConcurrentHashMap<>();

    //CONSTRUCTOR:

    public RepositorioConfiguracionCacheado(RepositorioConfiguracion repositorioBD) {
        this.repositorioBD = repositorioBD;
    }

    //MÉTODOS:

    @Override
    public String obtenerValorConfiguracion(String clave) {
        return this.cache.computeIfAbsent(clave, this.repositorioBD::obtenerValorConfiguracion);
    }

    @Override
    public ConfiguracionSistemaDTO obtenerConfiguracionSistema(String clave) {
        return this.cacheSistemas.computeIfAbsent(clave, this.repositorioBD::obtenerConfiguracionSistema);
    }

    @Override
    public void actualizarValorConfiguracion(String clave, String valor) {
        this.repositorioBD.actualizarValorConfiguracion(clave, valor);
        this.cache.remove(clave);
    }

    @Override
    public void actualizarConfiguracionSistemaConfiguracion(String clave, String valor, String descripcion) {
        this.repositorioBD.actualizarConfiguracionSistemaConfiguracion(clave, valor, descripcion);
        this.cacheSistemas.remove(clave);
    }

}//===================================================================================================================//

