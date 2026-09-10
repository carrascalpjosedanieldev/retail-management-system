package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.consultas.ConfiguracionSistemaDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.servicios.ServicioConfiguraciones;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

public class OrquestadorConfiguraciones {

    //ATRIBUTOS:

    private final ServicioConfiguraciones servicioConfiguraciones;

    //CONSTRUCTOR:

    public OrquestadorConfiguraciones(ServicioConfiguraciones servicioConfiguraciones) {
        this.servicioConfiguraciones = servicioConfiguraciones;
    }

    //MÉTODOS:

    public ConfiguracionSistemaDTO obtenerDatosTienda(){
        return this.servicioConfiguraciones.obtenerDatosTienda();
    }

    public void cambiarNombreYDescripcionTienda(
            UsuarioDTOCompleto usuario, String nuevoNombre, String nuevaDescripcion
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.EDITAR_POLITICAS_DE_BLOQUEO);
        this.servicioConfiguraciones.cambiarNombreYDescripcionTienda(
                usuario, nuevoNombre, nuevaDescripcion
        );
    }

    public ConfiguracionSistemaDTO obtenerMaxIntentosBloqueo(){
        return this.servicioConfiguraciones.obtenerMaxIntentosBloqueo();
    }

    public void actualizarMaxIntentos(UsuarioDTOCompleto usuario, int nuevoMaximo) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.EDITAR_POLITICAS_DE_BLOQUEO);
        this.servicioConfiguraciones.actualizarMaxIntentos(nuevoMaximo);
    }

    public ConfiguracionSistemaDTO obtenerMaxMinutosBloqueo(){
        return this.servicioConfiguraciones.obtenerMaxMinutosBloqueo();
    }

    public void actualizarMaxMinutosBloqueos(UsuarioDTOCompleto usuario, int nuevosMinutosBloqueo) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.EDITAR_POLITICAS_DE_BLOQUEO);
        this.servicioConfiguraciones.actualizarMaxMinutosBloqueos(nuevosMinutosBloqueo);
    }

}//===================================================================================================================//

