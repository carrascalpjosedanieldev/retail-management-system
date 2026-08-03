package RetailManagementSystem.infraestructura.configuracion;

import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.puertos.RepositorioConfiguracion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProveedorConfiguracionImpl implements ProveedorConfiguracion {

    //ATRIBUTOS:

    private final RepositorioConfiguracion repositorioConfiguracion;

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    //CONSTRUCTOR:

    public ProveedorConfiguracionImpl(RepositorioConfiguracion repositorioConfiguracion) {
        this.repositorioConfiguracion = repositorioConfiguracion;
    }

    //MÉTODOS:

    @Override
    public String obtenerValorConfiguracion(String clave) {
        return this.cache.computeIfAbsent(clave, key -> {
            String valorBD = this.repositorioConfiguracion.obtenerValorConfiguracion(key);
            if (valorBD == null) {
                throw new IllegalArgumentException("La Configuración de Clave -" + key + "- NO existe");
            }
            return valorBD;
        });
    }

    @Override
    public void invalidarCache(String clave) {
        this.cache.remove(clave);
    }

    @Override
    public void invalidarCacheCompleto() {
        this.cache.clear();
    }

}//===================================================================================================================//

