package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.servicios.ServicioInventario;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;
import RetailManagementSystem.dominio.entidades.Producto;

public class OrquestadorProductoInventario {

    //ATRIBUTOS:

    private final ServicioProductos servicioProductos;
    private final ServicioInventario servicioInventario;

    //CONSTRUCTOR:

    public OrquestadorProductoInventario(
            ServicioProductos servicioProductos,
            ServicioInventario servicioInventario
    ) {
        this.servicioProductos = servicioProductos;
        this.servicioInventario = servicioInventario;
    }

    //MÉTODOS:

    public void validarEspacioInventarioYGuardarProducto(int idInventario, Producto producto){
        this.servicioInventario.verificarEspacioDisponible(idInventario, producto.getStock());
        this.servicioProductos.registrarProducto(idInventario, producto);
    }

    public void validarEspacioInventarioYAumentarStockProducto(
            int idInventario, int cantidadAAumentarProducto, String codigoProducto
    ) {
        this.servicioInventario.verificarEspacioDisponible(idInventario, cantidadAAumentarProducto);
        this.servicioProductos.aumentarStockDeProductoDeInventario(
                idInventario, codigoProducto, cantidadAAumentarProducto
        );
    }

    public void validarEspacioInventarioYMoverProducto(
            int idInventarioSalida, int idInventarioDestino, String codigoProducto, int stockProducto
    ){
        this.servicioInventario.verificarEspacioDisponible(idInventarioDestino, stockProducto);
        this.servicioProductos.moverProductoAInventario(idInventarioSalida, idInventarioDestino, codigoProducto);
    }

}//===================================================================================================================//

