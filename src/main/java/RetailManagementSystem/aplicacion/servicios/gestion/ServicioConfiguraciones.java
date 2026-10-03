package RetailManagementSystem.aplicacion.servicios.gestion;

import RetailManagementSystem.aplicacion.dto.consultas.ConfiguracionSistemaDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.PoliticaDeBloqueoDTO;
import RetailManagementSystem.dominio.enums.ClaveConfiguracion;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioConfiguracion;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

public class ServicioConfiguraciones {

    //ATRIBUTOS:

    private final RepositorioConfiguracion repositorioConfiguracion;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioConfiguraciones(
            RepositorioConfiguracion repositorioConfiguracion, GestorTransaccional gestorTransaccional
    ) {
        this.repositorioConfiguracion = repositorioConfiguracion;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public String obtenerValorConfiguracion(String clave){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioConfiguracion.obtenerValorConfiguracion(clave)
        );
    }

    public ConfiguracionSistemaDTO obtenerConfiguracionSistema(String clave){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioConfiguracion.obtenerConfiguracionSistema(clave)
        );
    }

    private void actualizarValorConfiguracion(String clave, String valor){
        this.repositorioConfiguracion.actualizarValorConfiguracion(clave, valor);
    }

    //MÉTODOS ESPECÍFICOS:

    public String obtenerNombreTienda(){
        return obtenerValorConfiguracion(ClaveConfiguracion.DATOS_TIENDA.getClaveBD());
    }

    public ConfiguracionSistemaDTO obtenerDatosTienda(){
        return obtenerConfiguracionSistema(ClaveConfiguracion.DATOS_TIENDA.getClaveBD());
    }

    public void cambiarNombreYDescripcionTienda(String nombreNuevo, String descripcion) {
        if (nombreNuevo == null || nombreNuevo.isBlank()){
            throw new IllegalArgumentException("El Nombre de la Tienda NO puede estar Vacío.");
        }
        if (descripcion == null || descripcion.isBlank()){
            throw new IllegalArgumentException("La Description NO puede estar Vacía.");
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                this.repositorioConfiguracion.actualizarConfiguracionSistemaConfiguracion(
                        ClaveConfiguracion.DATOS_TIENDA.getClaveBD(), nombreNuevo.trim(), descripcion.trim()
                )
        );
    }

    public PoliticaDeBloqueoDTO obtenerPoliticaDeBloqueo(){
        ConfiguracionSistemaDTO maxIntentos =
                obtenerConfiguracionSistema(ClaveConfiguracion.MAX_INTENTOS_LOGIN.getClaveBD());
        ConfiguracionSistemaDTO minutosBloqueo =
                obtenerConfiguracionSistema(ClaveConfiguracion.MINUTOS_BLOQUEO.getClaveBD());
        return new PoliticaDeBloqueoDTO(maxIntentos, minutosBloqueo);
    }

    public void actualizarMaxIntentos(int nuevoMaximo) {
        if (nuevoMaximo <= 0){
            throw new IllegalArgumentException("Los Intentos Máximos son Inválidos");
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                actualizarValorConfiguracion(
                        ClaveConfiguracion.MAX_INTENTOS_LOGIN.getClaveBD(), String.valueOf(nuevoMaximo)
                )
        );
    }

    public void actualizarMaxMinutosBloqueos(int nuevosMinutosBloqueo) {
        if (nuevosMinutosBloqueo <= 0){
            throw new IllegalArgumentException("Los Minutos de Bloqueo son Inválidos");
        }
        this.gestorTransaccional.ejecutarEnTransaccion(()->
                actualizarValorConfiguracion(
                        ClaveConfiguracion.MINUTOS_BLOQUEO.getClaveBD(), String.valueOf(nuevosMinutosBloqueo)
                )
        );
    }

}//===================================================================================================================//

