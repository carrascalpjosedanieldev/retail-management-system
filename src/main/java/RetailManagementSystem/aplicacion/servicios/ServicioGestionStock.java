package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioInventario;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioProducto;

public class ServicioGestionStock {

    //ATRIBUTOS

    private final RepositorioProducto repositorioProducto;

    private final RepositorioInventario repositorioInventario;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioGestionStock(
            RepositorioProducto repositorioProducto, RepositorioInventario repositorioInventario,
            GestorTransaccional gestorTransaccional
    ) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioInventario = repositorioInventario;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    private Producto obtenerProductoDeInventario(int idInventario, String codigoProducto){
        return this.repositorioProducto.obtenerProductoDeInventario(idInventario, codigoProducto);
    }

    public void registrarProductoEnInventario(int idInventario, Producto producto){
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            this.repositorioInventario.validarCapacidadInventario(idInventario, producto.getStock());
            this.repositorioProducto.insertarProducto(producto, idInventario);
        });
    }

    public Producto aumentarStockDeProductoDeInventario(int idInventario, String codigoProducto, int cantidad){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Producto producto = obtenerProductoDeInventario(idInventario, codigoProducto);
            producto.aumentarStock(cantidad);
            this.repositorioInventario.validarCapacidadInventario(idInventario, cantidad);
            this.repositorioProducto.actualizarStockProducto(producto, idInventario);
            return producto;
        });
    }

    public Producto reducirStockDeProductoDeInventario(int idInventario, String codigoProducto, int cantidad){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Producto producto = obtenerProductoDeInventario(idInventario, codigoProducto);
            producto.reducirStock(cantidad);
            this.repositorioProducto.actualizarStockProducto(producto, idInventario);
            return producto;
        });
    }

}//===================================================================================================================//

