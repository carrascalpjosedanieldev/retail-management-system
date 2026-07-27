package ProyectoPropio1.infraestructura.inyeccion;

import ProyectoPropio1.aplicacion.orquestadores.OrquestadorProductoInventario;
import ProyectoPropio1.vista.controladores.menuPrincipal.MenuPrincipalControlador;
import ProyectoPropio1.vista.controladores.gestionarTienda.*;

import javafx.util.Callback;

public class FabricaControladores implements Callback<Class<?>, Object> {

    @Override
    public Object call(Class<?> claseControlador) {
        if (claseControlador == MenuPrincipalControlador.class) {
            return new MenuPrincipalControlador(
                    FabricaServicios.obtenerServicioConfiguraciones()
            );
        }
        if (claseControlador == CrearProductoControlador.class) {
            return new CrearProductoControlador(
                    FabricaServicios.obtenerServicioImpuestos(),
                    FabricaServicios.obtenerServicioDescuentos(),
                    FabricaServicios.obtenerServicioPoliticas(),
                    new OrquestadorProductoInventario(
                            FabricaServicios.obtenerServicioProductos(),
                            FabricaServicios.obtenerServicioInventario()
                    )
            );
        }
        if (claseControlador == EditarPerecederoControlador.class){
            return new EditarPerecederoControlador(
                    FabricaServicios.obtenerServicioImpuestos(),
                    FabricaServicios.obtenerServicioDescuentos(),
                    FabricaServicios.obtenerServicioPoliticas(),
                    FabricaServicios.obtenerServicioProductos(),
                    FabricaEnsambladores.obtenerEnsambladorDTOImpuesto(),
                    FabricaEnsambladores.obtenerEnsambladorDTODescuento(),
                    FabricaEnsambladores.obtenerEnsambladorDTOPoliticaVencimiento()
            );
        }
        if (claseControlador == EditarRopaControlador.class){
            return new EditarRopaControlador(
                    FabricaServicios.obtenerServicioImpuestos(),
                    FabricaServicios.obtenerServicioDescuentos(),
                    FabricaServicios.obtenerServicioProductos(),
                    FabricaEnsambladores.obtenerEnsambladorDTOImpuesto(),
                    FabricaEnsambladores.obtenerEnsambladorDTODescuento()
            );
        }
        if (claseControlador == GestionConfiguracionesControlador.class){
            return new GestionConfiguracionesControlador(FabricaServicios.obtenerServicioConfiguraciones());
        }
        if (claseControlador == GestionDescuentosControlador.class) {
            return new GestionDescuentosControlador(
                    FabricaServicios.obtenerServicioDescuentos(),
                    FabricaEnsambladores.obtenerEnsambladorDTODescuento()
            );
        }
        if (claseControlador == GestionImpuestosControlador.class) {
            return new GestionImpuestosControlador(
                    FabricaServicios.obtenerServicioImpuestos(),
                    FabricaEnsambladores.obtenerEnsambladorDTOImpuesto()
            );
        }
        if (claseControlador == GestionPoliticasVencimientoControlador.class){
            return new GestionPoliticasVencimientoControlador(
                    FabricaServicios.obtenerServicioPoliticas(),
                    FabricaEnsambladores.obtenerEnsambladorDTOPoliticaVencimiento()
            );
        }
        if (claseControlador == GestionInventariosControlador.class){
            return new GestionInventariosControlador(
                    FabricaServicios.obtenerServicioInventario(),
                    FabricaEnsambladores.obtenerEnsambladorDTOInventario()
            );
        }
        if (claseControlador == GestionServiciosControlador.class) {
            return new GestionServiciosControlador(
                    FabricaServicios.obtenerServicioServicios(),
                    FabricaServicios.obtenerServicioImpuestos(),
                    FabricaServicios.obtenerServicioDescuentos(),
                    FabricaEnsambladores.obtenerEnsambladorDTOServicio()
            );
        }
        if (claseControlador == TabGeneralProductosControlador.class){
            return new TabGeneralProductosControlador(
                    FabricaServicios.obtenerServicioProductos(),
                    FabricaServicios.obtenerServicioInventario(),
                    FabricaEnsambladores.obtenerEnsambladorDTOProducto(),
                    FabricaEnsambladores.obtenerEnsambladorDTOInventario()
            );
        }
        if (claseControlador == TabPerecederosControlador.class){
            return new TabPerecederosControlador(
                    FabricaServicios.obtenerServicioProductos(),
                    FabricaEnsambladores.obtenerEnsambladorDTOProducto()
            );
        }
        if (claseControlador == TabRopaControlador.class){
            return new TabRopaControlador(
                    FabricaServicios.obtenerServicioProductos(),
                    FabricaEnsambladores.obtenerEnsambladorDTOProducto()
            );
        }
        try {
            return claseControlador.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("NO se pudo Instanciar el Controlador: " + claseControlador.getName(), e);
        }
    }

}//===================================================================================================================//

