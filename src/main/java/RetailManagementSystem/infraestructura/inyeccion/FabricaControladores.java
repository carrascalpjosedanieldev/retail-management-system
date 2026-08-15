package RetailManagementSystem.infraestructura.inyeccion;

import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionRoles.*;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos.CrearDescuentoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos.EditarDescuentoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos.GestionDescuentosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos.CrearImpuestoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos.EditarImpuestoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos.GestionImpuestosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.gestionarProductos.*;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV.CrearPoliticaVencimiento;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV.EditarPoliticaVencimientoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV.GestionPoliticasVencimientoControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios.CrearServicioControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios.GestionServiciosControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.editarTienda.EdicionTiendaControlador;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios.*;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.gestionPermisos.PermisosVistaControlador;
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
                    ContenedorDependencias.getServicioImpuestos(),
                    ContenedorDependencias.getServicioDescuentos(),
                    ContenedorDependencias.getServicioPoliticaVencimiento(),
                    ContenedorDependencias.getOrquestadorProductoInventario()
            );
        }
        if (claseControlador == EditarPerecederoControlador.class){
            return new EditarPerecederoControlador(
                    ContenedorDependencias.getServicioImpuestos(),
                    ContenedorDependencias.getServicioDescuentos(),
                    ContenedorDependencias.getServicioPoliticaVencimiento(),
                    ContenedorDependencias.getServicioProductos(),
                    ContenedorDependencias.getEnsambladorDTOImpuesto(),
                    ContenedorDependencias.getEnsambladorDTODescuento(),
                    ContenedorDependencias.getEnsambladorDTOPoliticaVencimiento()
            );
        }
        if (claseControlador == EditarRopaControlador.class){
            return new EditarRopaControlador(
                    ContenedorDependencias.getServicioImpuestos(),
                    ContenedorDependencias.getServicioDescuentos(),
                    ContenedorDependencias.getServicioProductos(),
                    ContenedorDependencias.getEnsambladorDTOImpuesto(),
                    ContenedorDependencias.getEnsambladorDTODescuento()
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
                    ContenedorDependencias.getServicioProductos(),
                    ContenedorDependencias.getServicioInventario(),
                    ContenedorDependencias.getEnsambladorDTOProducto(),
                    ContenedorDependencias.getEnsambladorDTOInventario()
            );
        }
        if (claseControlador == TabPerecederosControlador.class){
            return new TabPerecederosControlador(
                    ContenedorDependencias.getServicioProductos(),
                    ContenedorDependencias.getEnsambladorDTOProducto()
            );
        }
        if (claseControlador == TabRopaControlador.class){
            return new TabRopaControlador(
                    ContenedorDependencias.getServicioProductos(),
                    ContenedorDependencias.getEnsambladorDTOProducto()
            );
        }
        if (claseControlador == PanelDeControlControlador.class){
            return new PanelDeControlControlador(
                    ContenedorDependencias.getServicioFacturas(),
                    ContenedorDependencias.getEnsambladorDTOFactura()
            );
        }
        if (claseControlador == HistorialVentasControlador.class){
            return new HistorialVentasControlador(
                    ContenedorDependencias.getServicioFacturas(),
                    ContenedorDependencias.getEnsambladorDTOFactura()
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
        if (claseControlador == PermisosVistaControlador.class) {
            return new PermisosVistaControlador(
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
                    ContenedorDependencias.getOrquestadorProductoInventario()
            );
        }
        if (claseControlador == EditarInventarioControlador.class){
            return new EditarInventarioControlador(
                    ContenedorDependencias.getOrquestadorProductoInventario()
            );
        }
        if (claseControlador == CrearServicioControlador.class){
            return new CrearServicioControlador(
                    ContenedorDependencias.getOrquestadorServicios()
            );
        }
        try {
            return claseControlador.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("NO se pudo Instanciar el Controlador: " + claseControlador.getName(), e);
        }
    }

}//===================================================================================================================//

