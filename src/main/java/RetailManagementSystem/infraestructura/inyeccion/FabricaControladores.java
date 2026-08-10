package RetailManagementSystem.infraestructura.inyeccion;

import RetailManagementSystem.vista.controladores.login.GestionRolesControlador;
import RetailManagementSystem.vista.controladores.login.PermisosVistaControlador;
import RetailManagementSystem.vista.controladores.menuPrincipal.*;
import RetailManagementSystem.vista.controladores.gestionarTienda.*;
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
                    ContenedorDependencias.getServicioDescuentos(),
                    ContenedorDependencias.getEnsambladorDTODescuento()
            );
        }
        if (claseControlador == GestionImpuestosControlador.class) {
            return new GestionImpuestosControlador(
                    ContenedorDependencias.getServicioImpuestos(),
                    ContenedorDependencias.getEnsambladorDTOImpuesto()
            );
        }
        if (claseControlador == GestionPoliticasVencimientoControlador.class){
            return new GestionPoliticasVencimientoControlador(
                    ContenedorDependencias.getServicioPoliticaVencimiento(),
                    ContenedorDependencias.getEnsambladorDTOPoliticaVencimiento()
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
                    ContenedorDependencias.getServicioServicios(),
                    ContenedorDependencias.getServicioImpuestos(),
                    ContenedorDependencias.getServicioDescuentos(),
                    ContenedorDependencias.getEnsambladorDTOServicio()
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
        try {
            return claseControlador.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("NO se pudo Instanciar el Controlador: " + claseControlador.getName(), e);
        }
    }

}//===================================================================================================================//

