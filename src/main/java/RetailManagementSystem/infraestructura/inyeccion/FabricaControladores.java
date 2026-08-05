package RetailManagementSystem.infraestructura.inyeccion;

import RetailManagementSystem.vista.controladores.menuPrincipal.*;
import RetailManagementSystem.vista.controladores.gestionarTienda.*;
import RetailManagementSystem.vista.controladores.puntoDeVenta.*;

import javafx.util.Callback;

public class FabricaControladores implements Callback<Class<?>, Object> {

    @Override
    public Object call(Class<?> claseControlador) {
        if (claseControlador == MenuPrincipalControlador.class) {
            return new MenuPrincipalControlador(
                    ContenedorRepositorios.getServicioConfiguraciones()
            );
        }
        if (claseControlador == CrearProductoControlador.class) {
            return new CrearProductoControlador(
                    ContenedorRepositorios.getServicioImpuestos(),
                    ContenedorRepositorios.getServicioDescuentos(),
                    ContenedorRepositorios.getServicioPoliticaVencimiento(),
                    ContenedorRepositorios.getOrquestadorProductoInventario()
            );
        }
        if (claseControlador == EditarPerecederoControlador.class){
            return new EditarPerecederoControlador(
                    ContenedorRepositorios.getServicioImpuestos(),
                    ContenedorRepositorios.getServicioDescuentos(),
                    ContenedorRepositorios.getServicioPoliticaVencimiento(),
                    ContenedorRepositorios.getServicioProductos(),
                    ContenedorRepositorios.getEnsambladorDTOImpuesto(),
                    ContenedorRepositorios.getEnsambladorDTODescuento(),
                    ContenedorRepositorios.getEnsambladorDTOPoliticaVencimiento()
            );
        }
        if (claseControlador == EditarRopaControlador.class){
            return new EditarRopaControlador(
                    ContenedorRepositorios.getServicioImpuestos(),
                    ContenedorRepositorios.getServicioDescuentos(),
                    ContenedorRepositorios.getServicioProductos(),
                    ContenedorRepositorios.getEnsambladorDTOImpuesto(),
                    ContenedorRepositorios.getEnsambladorDTODescuento()
            );
        }
        if (claseControlador == GestionDescuentosControlador.class) {
            return new GestionDescuentosControlador(
                    ContenedorRepositorios.getServicioDescuentos(),
                    ContenedorRepositorios.getEnsambladorDTODescuento()
            );
        }
        if (claseControlador == GestionImpuestosControlador.class) {
            return new GestionImpuestosControlador(
                    ContenedorRepositorios.getServicioImpuestos(),
                    ContenedorRepositorios.getEnsambladorDTOImpuesto()
            );
        }
        if (claseControlador == GestionPoliticasVencimientoControlador.class){
            return new GestionPoliticasVencimientoControlador(
                    ContenedorRepositorios.getServicioPoliticaVencimiento(),
                    ContenedorRepositorios.getEnsambladorDTOPoliticaVencimiento()
            );
        }
        if (claseControlador == GestionInventariosControlador.class){
            return new GestionInventariosControlador(
                    ContenedorRepositorios.getServicioInventario(),
                    ContenedorRepositorios.getEnsambladorDTOInventario()
            );
        }
        if (claseControlador == GestionServiciosControlador.class) {
            return new GestionServiciosControlador(
                    ContenedorRepositorios.getServicioServicios(),
                    ContenedorRepositorios.getServicioImpuestos(),
                    ContenedorRepositorios.getServicioDescuentos(),
                    ContenedorRepositorios.getEnsambladorDTOServicio()
            );
        }
        if (claseControlador == TabGeneralProductosControlador.class){
            return new TabGeneralProductosControlador(
                    ContenedorRepositorios.getServicioProductos(),
                    ContenedorRepositorios.getServicioInventario(),
                    ContenedorRepositorios.getEnsambladorDTOProducto(),
                    ContenedorRepositorios.getEnsambladorDTOInventario()
            );
        }
        if (claseControlador == TabPerecederosControlador.class){
            return new TabPerecederosControlador(
                    ContenedorRepositorios.getServicioProductos(),
                    ContenedorRepositorios.getEnsambladorDTOProducto()
            );
        }
        if (claseControlador == TabRopaControlador.class){
            return new TabRopaControlador(
                    ContenedorRepositorios.getServicioProductos(),
                    ContenedorRepositorios.getEnsambladorDTOProducto()
            );
        }
        if (claseControlador == PanelDeControlControlador.class){
            return new PanelDeControlControlador(
                    ContenedorRepositorios.getServicioFacturas(),
                    ContenedorRepositorios.getEnsambladorDTOFactura()
            );
        }
        if (claseControlador == HistorialVentasControlador.class){
            return new HistorialVentasControlador(
                    ContenedorRepositorios.getServicioFacturas(),
                    ContenedorRepositorios.getEnsambladorDTOFactura()
            );
        }
        if (claseControlador == MenuDeVentasControlador.class){
            return new MenuDeVentasControlador(
                    ContenedorRepositorios.getOrquestadorVentas()
            );
        }
        if (claseControlador == FacturaGeneradaControlador.class){
            return new FacturaGeneradaControlador(
                    ContenedorRepositorios.getServicioConfiguraciones()
            );
        }
        if (claseControlador == EdicionTiendaControlador.class){
            return new EdicionTiendaControlador(
                    ContenedorRepositorios.getServicioConfiguraciones()
            );
        }
        try {
            return claseControlador.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("NO se pudo Instanciar el Controlador: " + claseControlador.getName(), e);
        }
    }

}//===================================================================================================================//

