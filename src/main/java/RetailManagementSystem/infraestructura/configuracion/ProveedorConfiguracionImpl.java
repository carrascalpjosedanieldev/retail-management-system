package RetailManagementSystem.infraestructura.configuracion;

import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioConfiguracion;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProveedorConfiguracionImpl implements ProveedorConfiguracion {

    //ATRIBUTOS:

    private final GestorTransaccional gestorTransaccional;

    private final RepositorioConfiguracion repositorioConfiguracion;

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    //CONSTRUCTOR:

    public ProveedorConfiguracionImpl(
            GestorTransaccional gestorTransaccional, RepositorioConfiguracion repositorioConfiguracion
    ) {
        this.gestorTransaccional = gestorTransaccional;
        this.repositorioConfiguracion = repositorioConfiguracion;
    }

    //MÉTODOS:

    @Override
    public String obtenerValorConfiguracion(String clave) {
        return this.cache.computeIfAbsent(clave, key -> {
            String valorBD = this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                    this.repositorioConfiguracion.obtenerValorConfiguracion(key)
            );
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

