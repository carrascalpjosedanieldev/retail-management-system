package RetailManagementSystem.infraestructura.inyeccion;

import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles.*;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos.CrearDescuentoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos.EditarDescuentoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos.GestionDescuentosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos.CrearImpuestoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos.EditarImpuestoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos.GestionImpuestosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral.CrearProductoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral.ManejarStockControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral.MoverProductoAOtroInventarioControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabGeneral.TabGeneralProductosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabPerecedero.EditarPerecederoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabPerecedero.TabPerecederosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabRopa.EditarRopaControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.tabRopa.TabRopaControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV.CrearPoliticaVencimiento;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV.EditarPoliticaVencimientoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV.GestionPoliticasVencimientoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios.CrearServicioControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios.EditarServicioControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios.GestionServiciosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.editarTienda.EdicionTiendaControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.*;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionPermisos.GestionPermisosControlador;
import RetailManagementSystem.vista.controladores.gestionarUsuarios.*;
import RetailManagementSystem.vista.controladores.login.CambioContrasenaControlador;
import RetailManagementSystem.vista.controladores.login.LoginControlador;
import RetailManagementSystem.vista.controladores.menuPrincipal.*;
import RetailManagementSystem.vista.controladores.puntoDeVenta.*;

import javafx.util.Callback;

public class FabricaControladores implements Callback<Class<?>, Object> {

    @Override
    public Object call(Class<?> claseControlador) {
        if (claseControlador == MenuPrincipalControlador.class) {
            return new MenuPrincipalControlador(
                    ContenedorDependencias.getServicioConfiguraciones()
            );
        }
        if (claseControlador == CrearProductoControlador.class) {
            return new CrearProductoControlador(
                    ContenedorDependencias.getOrquestadorImpuestos(),
                    ContenedorDependencias.getOrquestadorDescuentos(),
                    ContenedorDependencias.getOrquestadorPoliticaVencimiento(),
                    ContenedorDependencias.getFabricaProductos(),
                    ContenedorDependencias.getOrquestadorGestionStock()
            );
        }
        if (claseControlador == EditarPerecederoControlador.class){
            return new EditarPerecederoControlador(
                    ContenedorDependencias.getOrquestadorImpuestos(),
                    ContenedorDependencias.getOrquestadorDescuentos(),
                    ContenedorDependencias.getOrquestadorPoliticaVencimiento(),
                    ContenedorDependencias.getOrquestadorProductos()
            );
        }
        if (claseControlador == EditarRopaControlador.class){
            return new EditarRopaControlador(
                    ContenedorDependencias.getOrquestadorImpuestos(),
                    ContenedorDependencias.getOrquestadorDescuentos(),
                    ContenedorDependencias.getOrquestadorProductos()
            );
        }
        if (claseControlador == GestionDescuentosControlador.class) {
            return new GestionDescuentosControlador(
                    ContenedorDependencias.getOrquestadorDescuentos()
            );
        }
        if (claseControlador == GestionImpuestosControlador.class) {
            return new GestionImpuestosControlador(
                    ContenedorDependencias.getOrquestadorImpuestos()
            );
        }
        if (claseControlador == GestionPoliticasVencimientoControlador.class){
            return new GestionPoliticasVencimientoControlador(
                    ContenedorDependencias.getOrquestadorPoliticaVencimiento()
            );
        }
        if (claseControlador == GestionInventariosControlador.class){
            return new GestionInventariosControlador(
                    ContenedorDependencias.getServicioInventario(),
                    ContenedorDependencias.getEnsambladorDTOInventario()
            );
        }
        if (claseControlador == GestionServiciosControlador.class) {
            return new GestionServiciosControlador(
                    ContenedorDependencias.getOrquestadorServicios(),
                    ContenedorDependencias.getOrquestadorDescuentos(),
                    ContenedorDependencias.getOrquestadorImpuestos()
            );
        }
        if (claseControlador == TabGeneralProductosControlador.class){
            return new TabGeneralProductosControlador(
                    ContenedorDependencias.getOrquestadorProductos()
            );
        }
        if (claseControlador == TabPerecederosControlador.class){
            return new TabPerecederosControlador(
                    ContenedorDependencias.getOrquestadorProductos()
            );
        }
        if (claseControlador == TabRopaControlador.class){
            return new TabRopaControlador(
                    ContenedorDependencias.getOrquestadorProductos()
            );
        }
        if (claseControlador == PanelDeControlControlador.class){
            return new PanelDeControlControlador(
                    ContenedorDependencias.getOrquestadorHistoricoDeVentas()
            );
        }
        if (claseControlador == HistorialVentasControlador.class){
            return new HistorialVentasControlador(
                    ContenedorDependencias.getOrquestadorHistoricoDeVentas()
            );
        }
        if (claseControlador == MenuDeVentasControlador.class){
            return new MenuDeVentasControlador(
                    ContenedorDependencias.getOrquestadorVentas()
            );
        }
        if (claseControlador == FacturaGeneradaControlador.class){
            return new FacturaGeneradaControlador(
                    ContenedorDependencias.getServicioConfiguraciones()
            );
        }
        if (claseControlador == EdicionTiendaControlador.class){
            return new EdicionTiendaControlador(
                    ContenedorDependencias.getServicioConfiguraciones()
            );
        }
        if (claseControlador == GestionPermisosControlador.class) {
            return new GestionPermisosControlador(
                    ContenedorDependencias.getOrquestadorPermisos()
            );
        }
        if (claseControlador == GestionRolesControlador.class){
            return new GestionRolesControlador(
                    ContenedorDependencias.getOrquestadorRoles()
            );
        }
        if (claseControlador == CrearRolNuevoControlador.class){
            return new CrearRolNuevoControlador(
                    ContenedorDependencias.getOrquestadorPermisos(),
                    ContenedorDependencias.getOrquestadorRoles()
            );
        }
        if (claseControlador == EditarDescuentoControlador.class){
            return new EditarDescuentoControlador(
                    ContenedorDependencias.getOrquestadorDescuentos()
            );
        }
        if (claseControlador == CrearDescuentoControlador.class){
            return new CrearDescuentoControlador(
                    ContenedorDependencias.getOrquestadorDescuentos()
            );
        }
        if (claseControlador == EditarImpuestoControlador.class){
            return new EditarImpuestoControlador(
                    ContenedorDependencias.getOrquestadorImpuestos()
            );
        }
        if (claseControlador == CrearImpuestoControlador.class){
            return new CrearImpuestoControlador(
                    ContenedorDependencias.getOrquestadorImpuestos()
            );
        }
        if (claseControlador == EditarPoliticaVencimientoControlador.class){
            return new EditarPoliticaVencimientoControlador(
                    ContenedorDependencias.getOrquestadorPoliticaVencimiento()
            );
        }
        if (claseControlador == CrearPoliticaVencimiento.class){
            return new CrearPoliticaVencimiento(
                    ContenedorDependencias.getOrquestadorPoliticaVencimiento()
            );
        }
        if (claseControlador == ModificarDatosRolControlador.class){
            return new ModificarDatosRolControlador(
                    ContenedorDependencias.getOrquestadorRoles()
            );
        }
        if (claseControlador == AdministrarPermisosDeRolControlador.class){
            return new AdministrarPermisosDeRolControlador(
                    ContenedorDependencias.getOrquestadorRoles()
            );
        }
        if (claseControlador == AnadirPermisoAlRolControlador.class){
            return new AnadirPermisoAlRolControlador(
                    ContenedorDependencias.getOrquestadorPermisos()
            );
        }
        if (claseControlador == CrearInventarioControlador.class){
            return new CrearInventarioControlador(
                    ContenedorDependencias.getOrquestadorInventarios()
            );
        }
        if (claseControlador == EditarInventarioControlador.class){
            return new EditarInventarioControlador(
                    ContenedorDependencias.getOrquestadorInventarios()
            );
        }
        if (claseControlador == CrearServicioControlador.class){
            return new CrearServicioControlador(
                    ContenedorDependencias.getOrquestadorServicios()
            );
        }
        if (claseControlador == EditarServicioControlador.class){
            return new EditarServicioControlador(
                    ContenedorDependencias.getOrquestadorServicios()
            );
        }
        if (claseControlador == ManejarStockControlador.class){
            return new ManejarStockControlador(
                    ContenedorDependencias.getOrquestadorGestionStock()
            );
        }
        if (claseControlador == MoverProductoAOtroInventarioControlador.class){
            return new MoverProductoAOtroInventarioControlador(
                    ContenedorDependencias.getOrquestadorGestionStock(),
                    ContenedorDependencias.getOrquestadorInventarios()
            );
        }
        if (claseControlador == GestionUsuariosControlador.class){
            return new GestionUsuariosControlador(
                    ContenedorDependencias.getOrquestadorUsuarios()
            );
        }
        if (claseControlador == RegistrarUsuarioControlador.class){
            return new RegistrarUsuarioControlador(
                    ContenedorDependencias.getOrquestadorUsuarios()
            );
        }
        if (claseControlador == EditarUsuarioControlador.class){
            return new EditarUsuarioControlador(
                    ContenedorDependencias.getOrquestadorUsuarios()
            );
        }
        if (claseControlador == RestablecerContrasenaControlador.class){
            return new RestablecerContrasenaControlador(
                    ContenedorDependencias.getOrquestadorUsuarios()
            );
        }
        if (claseControlador == GestionarRolesDeUsuarioControlador.class){
            return new GestionarRolesDeUsuarioControlador(
                    ContenedorDependencias.getOrquestadorUsuarios(),
                    ContenedorDependencias.getOrquestadorRoles()
            );
        }
        if (claseControlador == LoginControlador.class){
            return new LoginControlador(
                    ContenedorDependencias.getOrquestadorLogin()
            );
        }
        if (claseControlador == CambioContrasenaControlador.class){
            return new CambioContrasenaControlador(
                    ContenedorDependencias.getOrquestadorLogin()
            );
        }
        if (claseControlador == AumentarCapacidadControlador.class){
            return new AumentarCapacidadControlador(
                    ContenedorDependencias.getOrquestadorInventarios()
            );
        }
        try {
            return claseControlador.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("NO se pudo Instanciar el Controlador: " + claseControlador.getName(), e);
        }
    }

}//===================================================================================================================//

